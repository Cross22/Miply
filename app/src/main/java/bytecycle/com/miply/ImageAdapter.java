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
public class ImageAdapter extends ArrayAdapter<View> {
    public ImageAdapter(Context context) {
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
        ImageView sourceView= (ImageView) getItem(position);
        imageView.setImageDrawable(sourceView.getDrawable());

        return rowView;
    }

    // stable IDs
    private HashMap<View, Integer> mIdMap = new HashMap<View, Integer>();
    private Integer mCount=0;

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        View v= getItem(position);
        Integer stableId= mIdMap.get(v);
        if (stableId==null)
            return -1;
        return stableId;
    }

    @Override
    public void add(View object) {
        mIdMap.put(object, mCount++);
        super.add(object);
    }

    @Override
    public void remove(View object) {
        mIdMap.remove(object);
        super.remove(object);
    }

    @Override
    public void insert(View object, int index) {
        mIdMap.put(object, mCount++);
        super.insert(object, index);
    }
}
