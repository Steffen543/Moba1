package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.am.sk.passwordmanager.R;

import java.util.ArrayList;
import java.util.List;

import Model.CategoryModel;
import Model.ISearchable;
import Model.PasswordModel;

/**
 * Created by Steffen on 24.06.2017.
 */

public class SearchListAdapter extends BaseAdapter implements Filterable {
    private List<ISearchable> listData;
    private List<ISearchable> filterData;
    private LayoutInflater layoutInflater;
    private SearchListAdapter.ItemFilter mFilter = new SearchListAdapter.ItemFilter();

    public SearchListAdapter(Context aContext, List<ISearchable> listData) {
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

        ISearchable searchable = listData.get(i);

        SearchListAdapter.ViewHolder holder;

        if(searchable instanceof PasswordModel) {
            convertView = layoutInflater.inflate(R.layout.password_list_row_layout, null);
            holder = new SearchListAdapter.ViewHolder();
            holder.nameView = (TextView) convertView.findViewById(R.id.title);
            holder.descriptionView = (TextView) convertView.findViewById(R.id.subtitle);
            holder.addedView = (TextView) convertView.findViewById(R.id.righttext);
            PasswordModel password = (PasswordModel) searchable;
            holder.nameView.setText(password.getName());
            holder.descriptionView.setText(password.getEmail());
            holder.addedView.setText(password.getAdded().substring(0,10));
        }
        else  if(searchable instanceof CategoryModel){
            convertView = layoutInflater.inflate(R.layout.category_list_row_layout, null);
            holder = new SearchListAdapter.ViewHolder();
            holder.nameView = (TextView) convertView.findViewById(R.id.title);
            holder.descriptionView = (TextView) convertView.findViewById(R.id.description);
            holder.addedView = (TextView) convertView.findViewById(R.id.date);
            CategoryModel category = (CategoryModel) searchable;
            holder.nameView.setText(category.getName());
            holder.descriptionView.setText(Math.round(category.getChildCount()) + " Einträge");
            holder.addedView.setText(category.getAdded().substring(0,10));
        }
        else
        {
            System.out.println("############################################unimplemented type in getView()############################################");
            return null;
        }



        convertView.setTag(holder);



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

            final List<ISearchable> list = filterData;

            int count = list.size();
            final ArrayList<ISearchable> nlist = new ArrayList<ISearchable>(count);

            for (int i = 0; i < count; i++) {
                ISearchable searchObj = list.get(i);
                if (searchObj.search(filterString)) {
                    nlist.add(searchObj);
                    //System.out.println(searchCategory.getName() + " gefunden mit " + filterString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listData = (ArrayList<ISearchable>) results.values;
            notifyDataSetChanged();
        }

    }

    static class ViewHolder {
        TextView nameView;
        TextView descriptionView;
        TextView addedView;
    }
}
