
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.our_space.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.example.materialspinner.MaterialSpinner;


public class LocationSelection extends AppCompatActivity {

  private static final String[] ANDROID_VERSIONS = {"Riordan Auditorium", "Decker Hall - Basement", "Decker Hall - 1st Floor", "Decker Hall - 2nd Floor", "Decker Hall - 3rd Floor" };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);



    MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner);
    spinner.setItems(ANDROID_VERSIONS);
    spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

      @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
        Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
      }
    });
    spinner.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {

      @Override public void onNothingSelected(MaterialSpinner spinner) {
        Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show();
      }
    });
  }

}