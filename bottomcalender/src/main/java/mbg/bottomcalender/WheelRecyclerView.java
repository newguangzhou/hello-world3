package mbg.bottomcalender;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/12/31.
 */

public class WheelRecyclerView extends RecyclerView {
    private static final int CENTER = 0;
    private static final int LEFT_CENTER = 1;
    private static final int VERTICAL_CENTER = 2;

    /**
     * Scrolling duration
     */
    private static final int SCROLLING_DURATION = 400;

    /**
     * Minimum delta for scrolling
     */
    private static final int MIN_DELTA_FOR_SCROLLING = 1;

    /**
     * Current item text color
     */
    private int currentTextColor;

    /**
     * Items text color
     */
    private int normalTextColor;

    /**
     * Current item text size
     */
    private int normalTextSize;

    /**
     * Item text size
     */
    private int currentTextSize;

    private int currentItem = 0;

    // Count of visible items
    private int maxVisibleItemCount;

    private int middleIndex;

    // Scrolling
    private int scrollY;

    // Listener
    private OnWheelChangedListener mListener;

    private int itemHeight;

    private LinearLayoutManager layoutManager;

    private TextRecycledAdapter mAdapter;
    public WheelRecyclerView(Context context) {
        this(context,null);
    }

    public WheelRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public WheelRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttribute(context, attrs);
    }

    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WheelRecyclerView);
        currentTextColor = a.getColor(R.styleable.WheelRecyclerView_current_text_color, getResources().getColor(R.color.f79100));
        currentTextSize = a.getDimensionPixelSize(R.styleable.WheelRecyclerView_current_text_size, 36);
        normalTextColor = a.getColor(R.styleable.WheelRecyclerView_normal_text_color, 0xFF838383);
        normalTextSize = a.getDimensionPixelSize(R.styleable.WheelRecyclerView_normal_text_size, 32);
        int gravity = a.getInt(R.styleable.WheelRecyclerView_item_gravity, 0);
        if(gravity == CENTER){
            gravity = Gravity.CENTER;
        }else if(gravity == LEFT_CENTER){
            gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        }else{
            gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        }
        maxVisibleItemCount = a.getInt(R.styleable.WheelRecyclerView_max_visible_item_count, 5);
        a.recycle();
        if(maxVisibleItemCount % 2 == 0){
            throw new IllegalArgumentException("visibleItemCount must be an odd number");
        }
        middleIndex = maxVisibleItemCount / 2;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updateItemStyle();
            }
        });
        layoutManager = new LinearLayoutManager(context);
        setLayoutManager(layoutManager);
        setHasFixedSize(true);
        itemHeight = context.getResources().getDimensionPixelSize(R.dimen.picker_item_height);
        mAdapter = new TextRecycledAdapter(context, gravity);
        setAdapter(mAdapter);
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    justify();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollY += dy;
            }
        });
    }

    @Override
    public TextRecycledAdapter getAdapter() {
        return mAdapter;
    }

    public void setData(String[] data){
        if(mAdapter != null){
            if(data != null && data.length > 0){
                mAdapter.setData(data);
            }
        }
    }

    /**
     * set wheel changing listener
     *
     * @param listener the listener
     */
    public void setChangeListener(OnWheelChangedListener listener) {
        this.mListener = listener;
    }


    /**
     * Gets current value
     *
     * @return the current value
     */
    public int getCurrentItem() {
        return currentItem;
    }

    public String getCurrentValue(){
        return mAdapter.getItem(currentItem + 2);
    }

    /**
     * Sets the current item w/o animation. Does nothing when index is wrong.
     *
     * @param index the item index
     */
    public void setCurrentItem(int index) {
        currentItem = index;
        scrollToPosition(index);
    }

    /**
     * Justifies wheel
     */
    private void justify() {
        int offset = scrollY % itemHeight;
//        LogUtil.i(TAG, "justify, scrollY:" + scrollY + ", itemHeight:" + itemHeight + ", offset:" + offset);
        int absOffset = Math.abs(offset);
        if(absOffset > 0){
            //如果多余的部分超过一半,继续向前滑,否则倒滑
            if(absOffset > itemHeight / 2){
                if(offset > 0){
                    smoothScrollBy(0, itemHeight - offset);
                }else{
                    smoothScrollBy(0, -(itemHeight - absOffset));
                }
            }else{
                smoothScrollBy(0, -offset);
            }
        }else{
            int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            currentItem = firstVisibleItem + maxVisibleItemCount / 2 - 2;
            if(mListener != null){
                mListener.onChanged(this, currentItem);
            }
            updateItemStyle();
            scrollY = 0;
        }
    }

    private void updateItemStyle(){
        for(int i = 0; i < maxVisibleItemCount; i++){
            ViewHolder holder = findViewHolderForPosition(currentItem + i);
            if(holder == null){
                continue;
            }
            TextView tv = (TextView) holder.itemView.findViewById(R.id.picker_item_tv);
            int offset = Math.abs(i - middleIndex);
            if(offset == 2){
                tv.setTextColor(0xffc5c5c5);
            }else if(offset == 1){
                tv.setTextColor(normalTextColor);
            }else{
                tv.setTextColor(currentTextColor);
            }
        }
    }

    public interface OnWheelChangedListener {
        /**
         * Callback method to be invoked when current item changed
         *
         * @param wheel    the wheel view whose state has changed
         * @param current the index of current item
         */
        void onChanged(WheelRecyclerView wheel, int current);
    }
}
