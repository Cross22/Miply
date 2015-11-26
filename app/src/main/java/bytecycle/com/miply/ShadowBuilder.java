package bytecycle.com.miply;

import android.graphics.Point;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by marco on 11/22/15.
 */
public class ShadowBuilder extends View.DragShadowBuilder {
    public ShadowBuilder(View v) {
        super(v);
    }


    // move drag image up so it is visible above finger
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
        shadowSize.set(shadowSize.x*3, shadowSize.y*3);
//        shadowTouchPoint.set(shadowTouchPoint.x, (int)(shadowSize.y*1.5f) );
    }
}
