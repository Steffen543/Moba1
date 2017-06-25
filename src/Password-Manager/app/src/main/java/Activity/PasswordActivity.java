package Activity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hs_weingarten.sk.am.password_manager.R;

public class PasswordActivity extends Fragment{

    ListView passwordList;
    String[] passwords;
    int[] image = {R.drawable.ic_password};

    public PasswordActivity(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View folderView = inflater.inflate(R.layout.list_view, container, false);
        passwordList = (ListView) folderView.findViewById(R.id.listView);
        // here needs to be the call of the passwords from the Database
        // for example:
        // passwords = mydatabasehandler.getPasswords();
        PasswordActivity.MyPasswordAdapter passwordAdapter = new PasswordActivity.MyPasswordAdapter(getActivity(), passwords, image);
        passwordList.setAdapter(passwordAdapter);

        return folderView;
    }

    private class MyPasswordAdapter extends ArrayAdapter {
        int[] imageArray;
        String[] passwordsArray;

        MyPasswordAdapter(Context context, String[] passwordNames, int[] passwordImage) {
            super(context, R.layout.password_view, R.id.passwordName, passwordNames);
            this.imageArray = passwordImage;
            this.passwordsArray = passwordNames;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.password_view, parent, false);

            ImageView passwordImage = (ImageView) row.findViewById(R.id.passwordImage);
            TextView passwordName = (TextView) row.findViewById(R.id.passwordName);

            passwordImage.setImageResource(imageArray[position]);
            passwordName.setText(passwordsArray[position]);

            return row;
        }
    }
}
