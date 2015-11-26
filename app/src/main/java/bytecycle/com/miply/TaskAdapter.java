package bytecycle.com.miply;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by marco on 11/22/15.
 */
public class TaskAdapter extends ArrayAdapter<TaskItem> {
    public TaskAdapter(Context context) {
        super(context, -1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listrow, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        // show image with same drawable as the one we dragged up here
        imageView.setImageDrawable(getItem(position).getDrawable(getContext()));
        return rowView;
    }

    // stable IDs
    private HashMap<TaskItem, Integer> mIdMap = new HashMap<TaskItem, Integer>();
    private Integer mCount=0;

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        TaskItem t= getItem(position);
        Integer stableId= mIdMap.get(t);
        if (stableId==null)
            return -1;
        return stableId;
    }

    @Override
    public void add(TaskItem object) {
        mIdMap.put(object, mCount++);
        super.add(object);
    }

    @Override
    public void remove(TaskItem object) {
        mIdMap.remove(object);
        super.remove(object);
    }

    @Override
    public void insert(TaskItem object, int index) {
        mIdMap.put(object, mCount++);
        super.insert(object, index);
    }
}
