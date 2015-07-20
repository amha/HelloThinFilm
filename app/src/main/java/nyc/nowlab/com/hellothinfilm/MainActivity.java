package nyc.nowlab.com.hellothinfilm;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcBarcode;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    // UI Elements
    ProgressBar progress;
    TextView output;
    ImageView cta;

    // NFC related isntance variables
    NfcAdapter mNfcAdapter;
    NfcBarcode barcode;
    PendingIntent pendingIntent;
    IntentFilter[] intentFiltersArray;
    String[][] techListsArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get reference to output textview and set invisible
        output = (TextView) findViewById(R.id.output);

        // get reference to tap call to action image
        cta = (ImageView) findViewById(R.id.tap_cta);

        // get reference to progress bar
        progress = (ProgressBar) findViewById(R.id.progress);

        // Get NFC System Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);


        pendingIntent = PendingIntent.getActivity(
                this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter nfcTech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        try {
            nfcTech.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[]{nfcTech,};
        techListsArray = new String[][]{new String[]{NfcBarcode.class.getName()}};


    }


    @Override
    public void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this,
                pendingIntent,
                intentFiltersArray,
                techListsArray);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        // Reset output in case we're rescanning a tag
        output.setText("");
        output.setVisibility(View.INVISIBLE);

        barcode = NfcBarcode.get(tagFromIntent);
        final byte[] barcodeData = barcode.getBarcode();

        cta.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // hide progress bar after 1 second
                progress.setVisibility(View.INVISIBLE);

                // TODO: Create ListView with custom adapter
                // to show Barcode Byte Array
                output.setText(Arrays.toString(barcodeData));
                output.setVisibility(View.VISIBLE);
            }
        }, 1500);

    }
}
