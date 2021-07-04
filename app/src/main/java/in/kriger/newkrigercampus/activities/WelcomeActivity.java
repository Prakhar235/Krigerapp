package in.kriger.newkrigercampus.activities;


import androidx.fragment.app.Fragment;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.fragment.app.FragmentManager;
import in.kriger.newkrigercampus.bottomfragments.Krigers;
import in.kriger.newkrigercampus.fragments.ContactList;
import in.kriger.newkrigercampus.R;

public class WelcomeActivity extends AppCompatActivity {

    private int count = 0;

    Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


    }

    public void loadnextfragment(int count){
        if (count == 1)
        {

            setTitle("Suggestions");

            fragment = new Krigers();
            Bundle bundle = new Bundle();
            bundle.putInt("key", 1);
            fragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.welcome, fragment,"Krigers").commitAllowingStateLoss();

        }
        if (count == 2){
            setTitle("Send Invites");
            fragment = new ContactList();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.welcome, fragment,"ContactList").commitAllowingStateLoss();

        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_welcome_next:

               if (count == 1){
                    count++;
                    loadnextfragment(count);
                }else if (count == 2){

                    Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(intent);

                }



                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_welcome, menu);
        return true;
    }


}
