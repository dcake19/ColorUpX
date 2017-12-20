package com.dcake19.android.colorupx.game.view;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.dcake19.android.colorupx.game.controller.GameController;
import com.dcake19.android.colorupx.R;
import com.dcake19.android.colorupx.game.model.GameBoard;
import com.dcake19.android.colorupx.game.model.UpdateSquare;
import com.dcake19.android.colorupx.saving.SavedAnimatableRectF;
import com.dcake19.android.colorupx.saving.SavedFallingSquare;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GameView extends View
        implements GestureDetector.OnGestureListener{

    private GestureDetectorCompat mGestureDetector;
    protected GameController mController;
    protected int mMaxWidth,mMaxHeight;
    protected int mRows = 12;
    protected int mColumns = 6;
    protected int mBoardStartRow = 9;
    private float mSquareSideLength;
    private int mSquareCornerRadius = 0;
    private int mSquareMarginPx;
    private RectF mBackgroundRect;
    private Paint mBackgroundPaint;
    private Paint mEmptySquarePaint;
    private RectF[][] mEmptySquares;
    private AnimatableRectF[][] mSquares;
    private int mFallSquareDuration = 500;
    private int mMoveSquareDuration = 100;
    private int mMergeAnimationDuration = 200;
    private int mBackgroundColor;
    private int mEmptySquareColor;
    // the squares falling in the well
    protected Hashtable<Integer,SquareAnimation> mFallingSquares;
    private LinkedList<AnimatableRectF> mMoveFromWellToNewRow = new LinkedList<>();
    protected int mFlingLocks = 0;
    private int mLastDirection = 0;
    private LinkedList<Animator> mBoardAnimators = new LinkedList<>();
    private boolean mBoardAnimatorRunning = false;
    private boolean mModelUpdating = false;
    protected boolean mGamePaused = false;

    List<ScoreUpdateListener> mScoreUpdateListeners;
    List<GameOverListener> mGameOverListeners = new ArrayList<>();
    List<FallingSquareAddedListener> mFallingSquareAddedListeners = new ArrayList<>();

    public void addScoreUpdateListener(ScoreUpdateListener listener){
        mScoreUpdateListeners.add(listener);
    }

    public interface ScoreUpdateListener{
        void scoreUpdated(Integer score);
    }

    public void addGameOverListener(GameOverListener listener){
        mGameOverListeners.add(listener);
    }

    public interface GameOverListener{
        void gameOver();
    }

    public void addFallingSquareAddedListener(FallingSquareAddedListener listener){
        mFallingSquareAddedListeners.add(listener);
    }

    public interface FallingSquareAddedListener{
        void squareAdded(int column);
    }

    @TargetApi(21)
    public GameView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setAttributes(context, attrs);
        mScoreUpdateListeners = new ArrayList<>();
        initColors();
        init(context);
    }

    public GameView(Context context) {
        super(context);
        initColors();
        mScoreUpdateListeners = new ArrayList<>();
        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(context, attrs);
        initColors();
        mScoreUpdateListeners = new ArrayList<>();
        init(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttributes(context, attrs);
        initColors();
        mScoreUpdateListeners = new ArrayList<>();
        init(context);
    }

    private void setAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GameView,
                0, 0);
        try {
            mSquareCornerRadius = a.getDimensionPixelSize(R.styleable.GameView_cornerRadius, 0);
        } finally {
            a.recycle();
        }
    }

    private void initColors(){
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP){
            mBackgroundColor = getContext().getResources().getColor(R.color.colorBackground);
            mEmptySquareColor = getContext().getResources().getColor(R.color.colorEmpty);
        }else {
            initApi23Colors();
        }
    }

    @TargetApi(23)
    private void initApi23Colors(){
        mBackgroundColor = getContext().getColor(R.color.colorBackground);
        mEmptySquareColor = getContext().getColor(R.color.colorEmpty);
    }

    private void init(Context context){
        mGestureDetector = new GestureDetectorCompat(context,this);
        mFallingSquares = new Hashtable<>();
    }

    public void setParamters(int rows,int columns,int boardStartRow,int minBoardRows,int intialSquares,int maxSquareValue,int maxWidth,int maxHeight){
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mRows = rows;
        mColumns = columns;
        mBoardStartRow = boardStartRow;
        setDimensions();
        mGamePaused = true;
        setFallSquareDuration();
        mController = new GameController(this,rows,columns,boardStartRow,minBoardRows,intialSquares,maxSquareValue,0);
        invalidate();
    }

    public void loadGame(int[][] board,int score,int boardStartRow,int minBoardRows,int maxSquareValue,long delay,
                         SavedAnimatableRectF[] savedRects,int direction,SavedAnimatableRectF addSquareFromWell,
                         SavedFallingSquare[] fallingSquares,int maxWidth,int maxHeight){

        mFallingSquares.clear();
        mMoveFromWellToNewRow.clear();
        mBoardAnimators.clear();

        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mRows = board.length;
        mColumns = board[0].length;
        mBoardStartRow = boardStartRow;
        setFallSquareDuration();
        setDimensions();

        mGamePaused = true;

        mController = new GameController(this, board, score, boardStartRow, minBoardRows, maxSquareValue, delay, !(savedRects!=null && direction>0));

        if(savedRects!=null && direction>0){
            for(SavedAnimatableRectF sarf:savedRects){
                mSquares[sarf.iCoord][sarf.jCoord] = new AnimatableRectF(getContext(),
                        sarf.left,sarf.top,sarf.left+mSquareSideLength,
                        sarf.top+mSquareSideLength, mSquareCornerRadius,sarf.value);
            }
            mFlingLocks++;
            mController.swipeOnResumeAfterLoad(direction);
        }

        if(addSquareFromWell!=null) {
            AnimatableRectF rect = new AnimatableRectF(getContext(),
                    addSquareFromWell.left, addSquareFromWell.top, addSquareFromWell.left + mSquareSideLength,
                    addSquareFromWell.top + mSquareSideLength, mSquareCornerRadius, addSquareFromWell.value);
                mMoveFromWellToNewRow.add(rect);
            mFlingLocks++;
            mController.addFromWellOnResumeAfterLoad(addSquareFromWell.jCoord, addSquareFromWell.value, rect);
        }

        if(fallingSquares!=null){
            int key = 10000;
            for(SavedFallingSquare sfs:fallingSquares){
                addFallingSquare(sfs.column,key++,new AnimatableRectF(getContext(),
                        sfs.left, sfs.top, sfs.left + mSquareSideLength,
                        sfs.top + mSquareSideLength, mSquareCornerRadius,sfs.value));
            }
        }

        invalidate();
    }

    private void setFallSquareDuration(){
       mFallSquareDuration = 6000/mRows;
    }

    protected void setDimensions(){

        float maxSquareSize = Math.min(mMaxWidth/mColumns,mMaxHeight/mRows);
        mSquareMarginPx = (int)Math.round(maxSquareSize*0.10);

        mSquareSideLength = getSquareSideLength();

        mBackgroundRect = new RectF();
        mBackgroundRect.left = mSquareMarginPx;
        mBackgroundRect.top = 0;
        mBackgroundRect.right = 2*mSquareMarginPx + mColumns*(mSquareSideLength+mSquareMarginPx);
        mBackgroundRect.bottom = 2*mSquareMarginPx + mRows*(mSquareSideLength+mSquareMarginPx);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(mBackgroundColor);

        mEmptySquarePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mEmptySquarePaint.setColor(mEmptySquareColor);

        mEmptySquares = new RectF[mRows][mColumns];

        for(int i=0;i<mEmptySquares.length;i++){
            for(int j=0;j<mEmptySquares[i].length;j++){
                float xStart = getPxLocation(j);
                float yStart = getPxLocation(i);
                mEmptySquares[i][j] = new RectF(xStart,yStart,xStart+mSquareSideLength,yStart+mSquareSideLength);

            }
        }

        mSquares = new AnimatableRectF[mRows][mColumns];

        int maxDistance = Math.max(mRows-1,mColumns-1);
        mMoveSquareDuration = 300/maxDistance;
    }

    public float getPxLocation(int count){
        return 2*mSquareMarginPx+(mSquareMarginPx+mSquareSideLength)*count;
    }

    private float getPxChange(int change){
        return (mSquareMarginPx+mSquareSideLength)*change;
    }

    private int getIndexFromPxLocation(float pxLocation){
        return Math.round((pxLocation-2*mSquareMarginPx)/(mSquareMarginPx+mSquareSideLength));
    }

    private float getSquareSideLength(){
        int width = mMaxWidth;
        int height = mMaxHeight;
        width -= 4*mSquareMarginPx;
        height -= 4*mSquareMarginPx;
        float squareMaxWidth = width/(mColumns) - mSquareMarginPx;
        float squareMaxHeight = height/(mRows) - mSquareMarginPx;
        return Math.min(squareMaxWidth,squareMaxHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawRoundRect(mBackgroundRect,mSquareCornerRadius,mSquareCornerRadius,mBackgroundPaint);
        for(int i=0;i<mEmptySquares.length;i++)
            for(int j=0;j<mEmptySquares[i].length;j++) {
                if(i>=mBoardStartRow)
                    canvas.drawRoundRect(mEmptySquares[i][j],mSquareCornerRadius,mSquareCornerRadius, mEmptySquarePaint);
                else
                    canvas.drawRoundRect(mEmptySquares[i][j],mSquareCornerRadius,mSquareCornerRadius,mBackgroundPaint);
            }

        if(mLastDirection == GameBoard.DIRECTION_LEFT || mLastDirection == GameBoard.DIRECTION_UP) {
            for (int i = 0; i < mSquares.length; i++)
                for (int j = 0; j < mSquares[i].length; j++)
                    if (mSquares[i][j] != null) mSquares[i][j].drawToCanvas(canvas);
        }else {
            for (int i = mSquares.length-1; i >= 0; i--)
                for (int j = mSquares[i].length-1; j >= 0 ; j--)
                    if (mSquares[i][j] != null) mSquares[i][j].drawToCanvas(canvas);
        }

        Set<Integer> keySet = mFallingSquares.keySet();
        for(Integer i:keySet){
            mFallingSquares.get(i).rect.drawToCanvas(canvas);
        }

        for(AnimatableRectF rect:mMoveFromWellToNewRow){
            rect.drawToCanvas(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int)(mBackgroundRect.right+mSquareMarginPx),(int)(mBackgroundRect.bottom+mSquareMarginPx));
    }

    public void setBoard(int[][] board){
        for(int i=0;i<board.length;i++)
            for(int j=0;j<board[i].length;j++)
                if(board[i][j]!=0)
                    mSquares[i][j] = new AnimatableRectF(
                            getContext(),
                            getPxLocation(j),
                            getPxLocation(i),
                            getPxLocation(j) + mSquareSideLength,
                            getPxLocation(i) + mSquareSideLength,
                            mSquareCornerRadius,
                            board[i][j]);
        invalidate();
    }

    public void gameOver(){
        for(GameOverListener gol:mGameOverListeners){
            gol.gameOver();
        }
    }

    public void update(final ArrayList<UpdateSquare> updates, final int[][] updatedBoard, int lastDirection){
        mLastDirection = lastDirection;

        ArrayList<Animator> updateAnimations = new ArrayList<>();

        for(final UpdateSquare us:updates){
            AnimatorSet animatorSet = new AnimatorSet();

            float start = lastDirection == GameBoard.DIRECTION_LEFT || lastDirection == GameBoard.DIRECTION_RIGHT ?
                    mSquares[us.getStartRow()][us.getStartColumn()].left : mSquares[us.getStartRow()][us.getStartColumn()].top;

            ObjectAnimator animatorTranslate = ObjectAnimator.ofFloat(
                    mSquares[us.getStartRow()][us.getStartColumn()],
                    us.getPropertyName(),
                    start,
                    getPxLocation(us.getChangingLocation()+us.getSignedDistance()));
            animatorTranslate.setInterpolator(new LinearInterpolator());
            animatorTranslate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    invalidate();
                }
            });
            float distancePx;
            if(lastDirection==GameBoard.DIRECTION_LEFT) {
                distancePx = (mSquares[us.getStartRow()][us.getStartColumn()].left
                        - getPxLocation(us.getChangingLocation() + us.getSignedDistance()));
            }else if(lastDirection==GameBoard.DIRECTION_RIGHT) {
                distancePx = (getPxLocation(us.getChangingLocation() + us.getSignedDistance())
                        - mSquares[us.getStartRow()][us.getStartColumn()].left);
            }else if(lastDirection==GameBoard.DIRECTION_UP) {
                distancePx = (mSquares[us.getStartRow()][us.getStartColumn()].top
                        - getPxLocation(us.getChangingLocation() + us.getSignedDistance()));
            } else{
                distancePx = (getPxLocation(us.getChangingLocation() + us.getSignedDistance())
                        - mSquares[us.getStartRow()][us.getStartColumn()].top);
            }

            float distanceSquares = Math.round(distancePx/mSquareSideLength);
            int duration = distanceSquares > 0 ? Math.round(mMoveSquareDuration*distanceSquares): 0;
            animatorTranslate.setDuration(duration);
            if(us.getIncrease()){
                ObjectAnimator animatorScale = ObjectAnimator.ofFloat(
                        mSquares[us.getStartRow()][us.getStartColumn()],
                        "scale",
                        mEmptySquares[us.getEndRow()][us.getEndColumn()].bottom,
                        mEmptySquares[us.getEndRow()][us.getEndColumn()].bottom+mSquareMarginPx/2);
                animatorScale.setDuration(mMergeAnimationDuration);
                animatorScale.setRepeatCount(1);
                animatorScale.setRepeatMode(ValueAnimator.REVERSE);
                animatorScale.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        invalidate();
                    }
                });

                ObjectAnimator colorAnimator = ObjectAnimator.ofInt(mSquares[us.getStartRow()][us.getStartColumn()].getShapeForegroundPaint(),
                        "color",mSquares[us.getStartRow()][us.getStartColumn()].getColor(),mSquares[us.getStartRow()][us.getStartColumn()].getNextColor());

                colorAnimator.setDuration(mMergeAnimationDuration);
                colorAnimator.setEvaluator(new ArgbEvaluator());
                colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        invalidate();
                    }
                });
                animatorSet.play(animatorScale).with(colorAnimator).after(animatorTranslate);
            }else{
                animatorSet.play(animatorTranslate);
            }

            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }
                @Override
                public void onAnimationEnd(Animator animator) {
                    if(us.getIncrease()){
                        mSquares[us.getStartRow()][us.getStartColumn()].incrementValue();
                        invalidate();
                    }
                }
                @Override
                public void onAnimationCancel(Animator animator) {
                }
                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });

            updateAnimations.add(animatorSet);
        }

        AnimatorSet updatesAnimationsSet = new AnimatorSet();
        updatesAnimationsSet.playTogether(updateAnimations);

        updatesAnimationsSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mBoardAnimatorRunning = true;
                mModelUpdating = false;
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                for(UpdateSquare us:updates){
                    if(us.getIncrease()) {
                        mSquares[us.getEndRow()][us.getEndColumn()] = null;
                    }
                    mSquares[us.getEndRow()][us.getEndColumn()] = mSquares[us.getStartRow()][us.getStartColumn()];
                }

                for(int i=0;i<updatedBoard.length;i++) {
                    for (int j = 0; j < updatedBoard[i].length; j++) {
                        if(updatedBoard[i][j]==0) mSquares[i][j] = null;
                    }
                }
                invalidate();
                mBoardAnimators.removeFirst();
                mBoardAnimatorRunning = false;
                startAnimator(false);
                if(mFlingLocks!=0)mFlingLocks--;

                for(ScoreUpdateListener sul:mScoreUpdateListeners)
                    sul.scoreUpdated(mController.getScore());
            }
            @Override
            public void onAnimationCancel(Animator animator) {
            }
            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        addAnimator(updatesAnimationsSet,true);

    }

    public void moveFallingSquareToNewRow(final int newRow, final int column,final AnimatableRectF rect){
        // code should be obsolete but could potentially correct any squares in the wrong column
        final ObjectAnimator translateXAnimation =
                ObjectAnimator.ofFloat(rect,"translationX",
                        rect.left,getPxLocation(column));
        translateXAnimation.setDuration(1);

        final ObjectAnimator translateYAnimation =
                ObjectAnimator.ofFloat(rect,"translationY",
                        rect.top,getPxLocation(newRow));
        float distanceSquares =(getPxLocation(newRow) - rect.top)/mSquareSideLength;
        int duration = Math.round(mMoveSquareDuration*distanceSquares);
        translateYAnimation.setDuration(duration);
        translateYAnimation.setInterpolator(new LinearInterpolator());
        translateYAnimation.addUpdateListener(new ObjectAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });

        translateYAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mBoardAnimatorRunning = true;
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                mSquares[newRow][column] = rect;
                mMoveFromWellToNewRow.removeFirst();
                invalidate();
                mBoardAnimators.removeFirst();
                mBoardAnimatorRunning = false;
                startAnimator(false);

                if(mFlingLocks!=0)mFlingLocks--;

                for(FallingSquareAddedListener fsal:mFallingSquareAddedListeners){
                    fsal.squareAdded(column);
                }
            }
            @Override
            public void onAnimationCancel(Animator animator) {
            }
            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(translateXAnimation,translateYAnimation);
        //addAnimator(translateYAnimation,false);
        addAnimator(animation,false);
    }

    public void moveFallingSquareToNewRow(final int newRow, final int column, final int removeFallingSquaresIndex){
        SquareAnimation squareAnimation = mFallingSquares.remove(removeFallingSquaresIndex);
        mMoveFromWellToNewRow.add(squareAnimation.rect);
        moveFallingSquareToNewRow(newRow, column, squareAnimation.rect);
    }

    private synchronized void addAnimator(Animator animator,boolean modelUpdated){
        mBoardAnimators.add(animator);
        startAnimator(modelUpdated);
    }

    // called when an animation is finished or when a new animation is added
    public synchronized void startAnimator(boolean modelUpdated){
        if(!mGamePaused && mBoardAnimators.size()>0 && !mBoardAnimatorRunning && (!mModelUpdating || modelUpdated))
            if(!mBoardAnimators.getFirst().isStarted()) mBoardAnimators.getFirst().start();
    }

    public void addFallingSquare(int position,int value,int key){
       // Log.v("addFallingSquare1", "position: "+position+ " value: "+value + " key: "+key);
        float startX = getPxLocation(position);
        float startY = getPxLocation(-1) - mSquareMarginPx;

        AnimatableRectF square = new AnimatableRectF(getContext(),
                startX, startY,
                startX + mSquareSideLength,
                startY + mSquareSideLength,
                mSquareCornerRadius,value);

        addFallingSquare(position,key,square);

    }

    public void addFallingSquare(int column,int key,AnimatableRectF square){
        float startX = square.left;
        float endX = getPxLocation(column);

        final ObjectAnimator translateXAnimation =
                ObjectAnimator.ofFloat(square,"translationX",
                        startX,endX);
        if(startX != endX){
            float distancePx = Math.abs(startX-endX);
            int distanceSquares = Math.round(distancePx/mSquareSideLength);
            translateXAnimation.setDuration(distanceSquares*mMoveSquareDuration/mColumns);
            translateXAnimation.setInterpolator(new LinearInterpolator());
        }

        ObjectAnimator translateYAnimation =
                ObjectAnimator.ofFloat(square,"translationY",
                        square.top,getPxLocation(mBoardStartRow-1));

        float distancePx = getPxLocation(mBoardStartRow-1) - square.top;

        int distanceSquares = Math.round(distancePx/mSquareSideLength);
        translateYAnimation.setDuration(mFallSquareDuration*distanceSquares);
        translateYAnimation.setInterpolator(new LinearInterpolator());
        translateYAnimation.addUpdateListener(new ObjectAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });

        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(translateXAnimation,translateYAnimation);

        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFlingLocks++;
                Set<Integer> keySet = mFallingSquares.keySet();
                for(Integer i:keySet){
                    if(animation.equals(mFallingSquares.get(i).animator)) {
                        int column = mFallingSquares.get(i).column;
                        mController.addSquareFromWell(column,mFallingSquares.get(i).rect.getValue(),i);
                        break;
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        mFallingSquares.put(key,new SquareAnimation(column,square,animation));

        if(!mGamePaused)animation.start();
    }

    protected void swipeSquare(int direction,SquareAnimation squareAnimation){

        ArrayList<Animator> animators = squareAnimation.animator.getChildAnimations();
        ObjectAnimator objectAnimator = (ObjectAnimator) animators.get(0);
        float xValue = 0;
        int duration = 0;
        if(direction == GameBoard.DIRECTION_LEFT) {
            xValue = getPxLocation(0);
            duration = mMoveSquareDuration;
            squareAnimation.column = 0;
        }
        else if(direction == GameBoard.DIRECTION_RIGHT) {
            xValue = getPxLocation(mColumns - 1);
            duration = mMoveSquareDuration;
            squareAnimation.column = mColumns-1;
        }
        objectAnimator.setFloatValues(squareAnimation.rect.left,xValue);
        objectAnimator.setDuration(duration);
        objectAnimator.start();
    }

    protected void tapSquare(int direction,SquareAnimation squareAnimation){
        ArrayList<Animator> animators = squareAnimation.animator.getChildAnimations();
        ObjectAnimator objectAnimator = (ObjectAnimator) animators.get(0);
        float xValue = 0;

        if(direction == GameBoard.DIRECTION_LEFT &&
                squareAnimation.column != 0) {
            //xValue = squareAnimation.rect.left - getPxChange(1);
            xValue = getPxLocation(squareAnimation.column-1);
            squareAnimation.column--;
            objectAnimator.setFloatValues(squareAnimation.rect.left,xValue);
            objectAnimator.setDuration(mMoveSquareDuration);
            objectAnimator.start();
        }
        else if(direction == GameBoard.DIRECTION_RIGHT &&
                squareAnimation.column != mColumns - 1) {
            //xValue = squareAnimation.rect.left + getPxChange(1);
            xValue = getPxLocation(squareAnimation.column+1);
            squareAnimation.column++;
            objectAnimator.setFloatValues(squareAnimation.rect.left,xValue);
            objectAnimator.setDuration(mMoveSquareDuration);
            objectAnimator.start();
        }

    }

    public synchronized void increaseBoard(int column,int removeKey){
        mBoardAnimatorRunning = true;
        SquareAnimation squareAnimation = mFallingSquares.remove(removeKey);
        increaseBoard(column,squareAnimation.rect);
    }

    public synchronized void increaseBoard(int column,AnimatableRectF rect){
        mBoardStartRow--;
        mSquares[mBoardStartRow][column] = rect;
        changeFallingSquaresYTranslation();
        invalidate();
        if(mFlingLocks!=0) mFlingLocks--;
        for(FallingSquareAddedListener fsal:mFallingSquareAddedListeners){
            fsal.squareAdded(column);
        }
    }

    public void setWellRows(int wellRows){
        if(wellRows!=mBoardStartRow) {
            mBoardStartRow = wellRows;
            changeFallingSquaresYTranslation();
            invalidate();
        }
    }

    private void changeFallingSquaresYTranslation(){
        Set<Integer> keySet = mFallingSquares.keySet();

        for(Integer i:keySet){
            AnimatorSet animatorSet = mFallingSquares.get(i).animator;
            ArrayList<Animator> animators = animatorSet.getChildAnimations();
            ObjectAnimator objectAnimator = (ObjectAnimator) animators.get(1);
            objectAnimator.pause();
        }

        ArrayList<ObjectAnimator> objectAnimators = new ArrayList<>();
        for(Integer i:keySet){
            AnimatableRectF rect = mFallingSquares.get(i).rect;
            AnimatorSet animatorSet = mFallingSquares.get(i).animator;
            ArrayList<Animator> animators = animatorSet.getChildAnimations();
            ObjectAnimator objectAnimator = (ObjectAnimator) animators.get(1);
            float oldFallingSquareLocation = rect.top;
            objectAnimator.setFloatValues(oldFallingSquareLocation,getPxLocation(mBoardStartRow-1));
            int duration = (int)(mFallSquareDuration*(getPxLocation(mBoardStartRow-1)-oldFallingSquareLocation)/mSquareSideLength);
            objectAnimator.setDuration(duration > 0 ? duration:mMoveSquareDuration);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimators.add(objectAnimator);
        }

        for(ObjectAnimator oa:objectAnimators){
            oa.start();
        }

        mBoardAnimatorRunning = false;

    }

    public void allowFling(){
        if(mFlingLocks!=0)mFlingLocks--;
    }

    public void modelFinishedUpdating(){
        mModelUpdating = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        mGestureDetector.onTouchEvent(event);
        return  true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        //if(!mGamePaused && motionEvent.getY() < getPxLocation(mBoardStartRow-2)) {
        if(!mGamePaused && motionEvent.getY() < getPxLocation(mBoardStartRow-1)) {
            Set<Integer> keySet = mFallingSquares.keySet();
            for (Integer i : keySet) {
                AnimatableRectF rect = mFallingSquares.get(i).rect;

                if ((motionEvent.getY() >= rect.top - 0.5 * mSquareSideLength) &&
                        (motionEvent.getY() <= rect.bottom + 0.5 * mSquareSideLength)) {
                    int direction = 0;
                    if (motionEvent.getX() <= rect.left)
                        direction = GameBoard.DIRECTION_LEFT;
                    else if (motionEvent.getX() >= rect.right)
                        direction = GameBoard.DIRECTION_RIGHT;

                    if (direction != 0)
                        tapSquare(direction, mFallingSquares.get(i));
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent motionEvent1, MotionEvent motionEvent2, float velocityX, float velocityY) {
        if(!mGamePaused) {
            // true if the fling is in the well, false if in the board
            //boolean well = motionEvent1.getY() < getPxLocation(mBoardStartRow - 2);
            boolean well = motionEvent1.getY() < getPxLocation(mBoardStartRow - 1);
            boolean board = motionEvent1.getY() > getPxLocation(mBoardStartRow - 1);
            float horizontal = motionEvent1.getX() - motionEvent2.getX();
            float vertical = motionEvent1.getY() - motionEvent2.getY();

            if (well && Math.abs(horizontal)>Math.abs(vertical)) {
                Set<Integer> keySet = mFallingSquares.keySet();

                int direction = horizontal > 0 ? GameBoard.DIRECTION_LEFT : GameBoard.DIRECTION_RIGHT;

                for (Integer i : keySet) {
                    AnimatableRectF rect = mFallingSquares.get(i).rect;
                    if ((motionEvent1.getY() >= rect.top - 0.5 * mSquareSideLength) &&
                            (motionEvent1.getY() <= rect.bottom + 0.5 * mSquareSideLength)) {
                        swipeSquare(direction, mFallingSquares.get(i));
                        break;
                    }
                }
            } else if (mFlingLocks == 0 && board) {
                mModelUpdating = true;
                mFlingLocks++;

                if (Math.abs(horizontal) > Math.abs(vertical)) {
                    if (horizontal > 0)
                        mController.swipe(GameBoard.DIRECTION_LEFT);
                    else
                        mController.swipe(GameBoard.DIRECTION_RIGHT);
                } else {
                    if (vertical > 0)
                        mController.swipe(GameBoard.DIRECTION_UP);
                    else
                        mController.swipe(GameBoard.DIRECTION_DOWN);
                }
            }
        }
        return true;
    }

    public int[][] getViewBoard(){
        int[][] board = new int[mRows][mColumns];

        for (int i = 0; i < mSquares.length; i++)
            for (int j = 0; j < mSquares[i].length; j++) {
                if(mSquares[i][j]!=null)
                    board[i][j] = mSquares[i][j].getValue();
                else
                    board[i][j] = 0;
            }

        return board;
    }

    public int getScore(){
        return mController.getScore();
    }

    public int getBoardStartRow() {
        return mBoardStartRow;
    }

    public int getMinBoardRows(){
        return mController.getMinBoardRows();
    }

    public int getMaxSquareValue(){
        return mController.getMaxSquarevalue();
    }

    public long getDelay(){
        Log.v("GameView", "delay time: " + mController.getRemainingTimeForDelay());
        return mController.getRemainingTimeForDelay();
    }

    public SavedAnimatableRectF[] getCurrentBoardPosition(){

        ArrayList<SavedAnimatableRectF> rects = new ArrayList<>();

        for(int i=0;i<mSquares.length;i++) {
            for (int j = 0; j < mSquares[i].length; j++) {
                if(mSquares[i][j]!=null)
                    rects.add(new SavedAnimatableRectF(i,j,
                            mSquares[i][j].left,mSquares[i][j].top,
                            mSquares[i][j].getValue()));
            }
        }
        SavedAnimatableRectF[] cbp = new SavedAnimatableRectF[rects.size()];

        for(int k=0;k<cbp.length;k++){
            cbp[k] = rects.get(k);
        }

        return cbp;
    }

    public int getDirection(){
        return mBoardAnimators.size() > mMoveFromWellToNewRow.size() ? mLastDirection : 0;
    }

    public SavedAnimatableRectF getMoveFromWellToNewRow(){
        if( mMoveFromWellToNewRow.size() == 0)
            return null;

        AnimatableRectF rect = mMoveFromWellToNewRow.get(0);

        return new SavedAnimatableRectF(mBoardStartRow-1,
                getIndexFromPxLocation(rect.left),
                rect.left, rect.top,
                rect.getValue());
    }

    public SavedFallingSquare[] getFallingSquares(){
        SavedFallingSquare[] fallingSquares = new SavedFallingSquare[mFallingSquares.size()];

        Set<Integer> keySet = mFallingSquares.keySet();
        int index = 0;
        for(Integer i:keySet){
           fallingSquares[index] = new SavedFallingSquare(mFallingSquares.get(i).rect.left,
                   mFallingSquares.get(i).rect.top,
                   mFallingSquares.get(i).rect.getValue(),
                   mFallingSquares.get(i).column);
            index++;
        }
        return fallingSquares;
    }

    public boolean isGamePaused() {
        return mGamePaused;
    }

    public void pause(){
        if(!mGamePaused) {
            mGamePaused = true;
            mController.pause();
            if(mBoardAnimators.size()>0) mBoardAnimators.getFirst().pause();
            Set<Integer> keySet = mFallingSquares.keySet();
            for (Integer i : keySet) {
                mFallingSquares.get(i).animator.pause();
            }
        }
    }

    public void stop(){
        mController.stop();
    }

    public void resume(){
        if(mGamePaused){
            mGamePaused = false;
            Set<Integer> keySet = mFallingSquares.keySet();
            for (Integer i : keySet) {
                if(mFallingSquares.get(i).animator.isStarted())
                    mFallingSquares.get(i).animator.resume();
                else
                    mFallingSquares.get(i).animator.start();
            }
            if(mBoardAnimators.size()>0) mBoardAnimators.getFirst().resume();
            mController.resume();
        }
    }

    public void startNewGame(){
        setDimensions();
        mGamePaused = true;
        mBoardAnimatorRunning = false;
        mModelUpdating = false;
        mFallingSquares = new Hashtable<>();
        mFlingLocks = 0;
        mMoveFromWellToNewRow.clear();
        mBoardAnimators.clear();
        mLastDirection = 0;
        mBoardStartRow = mRows-3;
        mController.stopFallingSquares();
        mController = new GameController(this,mRows,mColumns,mBoardStartRow,3,8,11,0);
        invalidate();
    }

    protected class SquareAnimation{
        int column;
        AnimatableRectF rect;
        AnimatorSet animator;

        public SquareAnimation(int column,AnimatableRectF rect, AnimatorSet animator) {
            this.column = column;
            this.rect = rect;
            this.animator = animator;
        }
    }

}
