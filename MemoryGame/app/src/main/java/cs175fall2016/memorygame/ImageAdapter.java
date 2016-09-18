package cs175fall2016.memorygame;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Integer> data;
    private boolean[] done;
    private Configuration config;

    public ImageAdapter(Context context, ArrayList<Integer> data, boolean[] done) {
        this.context = context;
        this.data = data;
        this.done = done;
        config = context.getResources().getConfiguration();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);

            if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                imageView.setLayoutParams(new GridView.LayoutParams(80, 80));
            }
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        // Set image based on status.
        if (done[position]) {
            imageView.setImageResource(data.get(position));
            imageView.setAlpha(0.4f);
        } else {
            imageView.setImageResource(R.drawable.oh_00);
        }

        return imageView;
    }
}
