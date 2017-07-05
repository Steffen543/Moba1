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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import Model.PasswordModel;


public class PasswordListAdapter extends BaseAdapter implements Filterable {

    private List<PasswordModel> listData;
    private List<PasswordModel> filterData;
    private LayoutInflater layoutInflater;
    private PasswordListAdapter.ItemFilter mFilter = new PasswordListAdapter.ItemFilter();

    public PasswordListAdapter(Context aContext, List<PasswordModel> listData) {
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
        PasswordListAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.password_list_row_layout, null);
            holder = new PasswordListAdapter.ViewHolder();
            holder.nameView = (TextView) convertView.findViewById(R.id.title);
            holder.descriptionView = (TextView) convertView.findViewById(R.id.subtitle);
            holder.addedView = (TextView) convertView.findViewById(R.id.righttext);
            convertView.setTag(holder);
        } else {
            holder = (PasswordListAdapter.ViewHolder) convertView.getTag();
        }

        PasswordModel pwd = listData.get(i);

        holder.nameView.setText(pwd.getName());

        String email = pwd.getEmail();
        if(email.length() > 20)
            holder.descriptionView.setText(email.substring(0, 20) + "..");
        else
            holder.descriptionView.setText(email);

        holder.addedView.setText(pwd.getAdded().substring(0,10));


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

            final List<PasswordModel> list = filterData;

            int count = list.size();
            final ArrayList<PasswordModel> nlist = new ArrayList<PasswordModel>(count);

            for (int i = 0; i < count; i++) {
                PasswordModel searchPassword = list.get(i);
                if (searchPassword.getName().toLowerCase().contains(filterString)) {
                    nlist.add(searchPassword);

                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listData = (ArrayList<PasswordModel>) results.values;
            notifyDataSetChanged();
        }

    }

    static class ViewHolder {
        TextView nameView;
        TextView descriptionView;
        TextView addedView;
    }
}
