package Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.am.sk.passwordmanager.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import Model.CategoryModel;

/**
 * Created by Steffen on 22.06.2017.
 */

public class CategoryListAdapter extends BaseAdapter implements Filterable {

    private List<CategoryModel> listData;
    private List<CategoryModel> filterData;
    private LayoutInflater layoutInflater;
    private ItemFilter mFilter = new ItemFilter();

    public CategoryListAdapter(Context aContext, List<CategoryModel> listData) {
        this.listData = listData;
        filterData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }
    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return listData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.category_list_row_layout, null);
            holder = new ViewHolder();
            holder.nameView = (TextView) convertView.findViewById(R.id.title);
            holder.descriptionView = (TextView) convertView.findViewById(R.id.description);
            holder.addedView = (TextView) convertView.findViewById(R.id.date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CategoryModel category = listData.get(i);

        holder.nameView.setText(category.getName());
        holder.descriptionView.setText(Math.round(category.getChildCount()) + " Einträge");
        holder.addedView.setText(category.getAdded().substring(0,10));
        return convertView;
    }

    @Override
    public Filter getFilter() {

        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<CategoryModel> list = filterData;

            int count = list.size();
            final ArrayList<CategoryModel> nlist = new ArrayList<CategoryModel>(count);

            for (int i = 0; i < count; i++) {
                CategoryModel searchCategory = list.get(i);
                if (searchCategory.getName().toLowerCase().contains(filterString)) {
                    nlist.add(searchCategory);
                    System.out.println(searchCategory.getName() + " gefunden mit " + filterString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listData = (ArrayList<CategoryModel>) results.values;
            notifyDataSetChanged();
        }

    }

    static class ViewHolder {
        TextView nameView;
        TextView descriptionView;
        TextView addedView;
    }
}


