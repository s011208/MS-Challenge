package bj4.yhh.mschallenge.agenda;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by yenhsunhuang on 2016/6/6.
 */
public class AgendaView extends ListView {
    public AgendaView(Context context) {
        this(context, null);
    }

    public AgendaView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AgendaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
