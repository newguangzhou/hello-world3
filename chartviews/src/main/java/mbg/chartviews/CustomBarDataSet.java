package mbg.chartviews;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

/**
 * Created by Administrator on 2017/1/9.
 */

public class CustomBarDataSet extends BarDataSet {
    public CustomBarDataSet(List<BarEntry> yVals, String label) {
        super(yVals, label);
    }

    private boolean[] isFills;
    private boolean isFill=true;
    private float interval=0;
    private boolean isSlashPositive=false;

    public void setIsFills(boolean[] isFills){
        this.isFills=isFills;
    }

    public boolean isFill(int index){
        if(null == isFills || isFills.length <= 0){
            return isFill;
        }
        return isFills[index%isFills.length];
    }

    public  void setFill(boolean isFill){
        this.isFill=isFill;
    }

    public boolean isFill(){
        return isFill;
    }

    public void setSlashInterval(float interval){
        this.interval=interval;
    }

    public float getSlashInterval(){
        return this.interval;
    }

    public void setSlashPositive(boolean isSlashPositive){
        this.isSlashPositive=isSlashPositive;
    }

    public int getSlashDegree(){
        if (isSlashPositive){
            return 45;
        }
        return -45;
    }

}
