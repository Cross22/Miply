package bytecycle.com.miply;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

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

        final TaskItem item= getItem(position);
        // Icon depends on task type
        imageView.setImageDrawable(item.getDrawable(getContext()));
        final ImageButton btn = (ImageButton) rowView.findViewById(R.id.colorButton);

        // When user clicks on color button they get a color picker to choose from
        if (item.taskType== TaskItem.TaskType.COLOR) {
            btn.setBackgroundColor(item.value);
            btn.setVisibility(View.VISIBLE);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ColorPickerDialogBuilder
                            .with(getContext())
                            .setTitle("Pick a color")
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .initialColor(item.value)
                            .setPositiveButton("OK", new ColorPickerClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int selectedColor,
                                                    Integer[] allColors) {
                                    btn.setBackgroundColor(selectedColor);
                                }
                            })
                            .build()
                            .show();
                }
            });
        }
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
