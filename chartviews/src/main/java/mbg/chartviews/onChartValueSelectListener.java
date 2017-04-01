package mbg.chartviews;

import java.util.List;

/**
 * Created by Administrator on 2017/1/8.
 */

public interface onChartValueSelectListener {
    void onChartValueSelect(int index,String xLabel,List<Float> values);
    void onCancleSelect();
}
