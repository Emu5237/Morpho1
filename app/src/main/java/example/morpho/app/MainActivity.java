package example.morpho.app;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbManager;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.syscom.MorphoSmartLite.Callback;
import com.syscom.MorphoSmartLite.MSODevice;
import com.syscom.MorphoSmartLite.MorphoSmartErrors;

import java.io.IOException;

//import com.syscom.MorphoSmartLite.CallbackR;

public class MainActivity extends AppCompatActivity implements Callback, View.OnClickListener {
    private TextView template_tv, messages;
    private Button permission, capture_btn, cancel_btn, register, unregister, isregistered, modelnumber, serialnumber;
   // private final MSODevice msoDevice = MSODevice.getInstance(hexStringToByteArray("463324563252365653663566356356735635636535662547"));
    private final MSODevice msoDevice = MSODevice.getInstance(hexStringToByteArray("112233445566778899112233445566778899112233445566"));
    private IntentFilter filter = new IntentFilter();
    private boolean isRegistered = false;
    private String serialNumber = null,  modelNumber = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        template_tv = (TextView) findViewById(R.id.template_tv);
        messages = (TextView) findViewById(R.id.messages);

        permission = (Button) findViewById(R.id.permission);
        register = (Button) findViewById(R.id.register);
        unregister = (Button) findViewById(R.id.unregister);
        isregistered = (Button) findViewById(R.id.isregistered);
        modelnumber = (Button) findViewById(R.id.modelnumber);
        serialnumber = (Button) findViewById(R.id.serialnumber);
        capture_btn = (Button) findViewById(R.id.capture);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);

        permission.setOnClickListener(this);
        register.setOnClickListener(this);
        unregister.setOnClickListener(this);
        isregistered.setOnClickListener(this);
        modelnumber.setOnClickListener(this);
        serialnumber.setOnClickListener(this);
        capture_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);

        registerForContextMenu(template_tv);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);

        addListenerOnButtonClick();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRegistered)
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegistered)
        unregisterReceiver(broadcastReceiver);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                Callback callback = MainActivity.this;
                try
                {
                    registerReceiver(broadcastReceiver, filter);
                    isRegistered = true;
                    msoDevice.requestPermission(callback, MainActivity.this);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    @SuppressWarnings("deprecation")
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //user has long pressed your TextView
        menu.add(0, v.getId(), 0, "Copy");

        //cast the received View to TextView so that you can get its text
        TextView yourTextView = (TextView) v;

        //place your TextView's text in clipboard
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboard.setText(yourTextView.getText());
    }

    @Override
    public void onResponse(final byte[] response) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                template_tv.setText(byteToBitmap(response).getByteCount());
            }
        });
    }

    @Override
    public void onRegistered(final MorphoSmartErrors.Response respCode) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, respCode.name(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUnlocked(final MorphoSmartErrors.Response respCode) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, respCode.name(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLocked(final MorphoSmartErrors.Response respCode) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, respCode.name(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onQualityResponse(final int quality){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, Integer.toString(quality), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFailure(final MorphoSmartErrors.Errors errorCode) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, errorCode.name(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCancelled(final MorphoSmartErrors.Errors errorCode)
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, errorCode.name(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPermissionRequired(final MorphoSmartErrors.Errors errorCode)
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, errorCode.name(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPermissionDenied(final MorphoSmartErrors.Errors errorCode)
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, errorCode.name(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPermissionGranted(final MorphoSmartErrors.Response respCode)
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, respCode.name(), Toast.LENGTH_SHORT).show();
                template_tv.setText(msoDevice.sdkVersion());
            }
        });
    }

    @Override
    public void onMessages(final MorphoSmartErrors.Messages message)
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messages.setText( message.name());
            }
        });
    }

    @Override
    public void onClick(final View view) {
        final String registerPayload = "AC3800B80A1FBA85B0C9DE4D97DC0E04D1A8CBB1D7922387D7318D1471F628A6D46612EAD07700ECC4FB22548EBEAAE5133BA13A4B203E23B5C1FD";
        final String unRegisterPayload = "AC38000D4B12B166ABF9BE2FE25D3F89DC092505434345C3592BA9733EE4D80BB2EBCE462E697EE2DEBF74D04038322DD435BC16C08B4D062CC24C";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Callback callback = MainActivity.this;
                    switch (view.getId())
                    {
                        case R.id.permission:
                            msoDevice.requestPermission(callback, MainActivity.this);
                            break;
                        case R.id.register:
                            msoDevice.register(callback, MainActivity.this, hexStringToByteArray(registerPayload));
                            break;
                        case R.id.unregister:
                            msoDevice.unRegsiter(callback, MainActivity.this, hexStringToByteArray(unRegisterPayload));
                            break;
                        case R.id.isregistered:
                            msoDevice.isRegistered(callback, MainActivity.this);
                            break;
//                        case R.id.modelnumber:
//                            modelNumber = msoDevice.getDeviceModel(callback, MainActivity.this);
//                            break;
//                        case R.id.serialnumber:
//                            serialNumber = msoDevice.getDeviceSerialNumber(callback, MainActivity.this);
//                            break;
                        case R.id.capture:
                            msoDevice.capture(10,callback, MainActivity.this);
                            break;
                        case R.id.cancel_btn:
                            msoDevice.cancelLiveAcquisition(callback, MainActivity.this);
                            break;
                    }
                } catch (IOException  | UnsupportedOperationException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void addListenerOnButtonClick()
    {
        final Callback callback = MainActivity.this;

        modelnumber.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    modelNumber = msoDevice.getDeviceModel(callback, MainActivity.this);
                    write(modelNumber);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        serialnumber.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    serialNumber = msoDevice.getDeviceSerialNumber(callback, MainActivity.this);
                    write(serialNumber);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void write(CharSequence text)
    {
        //final TextView ftv = view;
        final TextView ftv = template_tv;
        final CharSequence ftext = text;

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ftv.setText(ftext);
            }
        });
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static String byteArrayToHexString(byte[] array) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : array) {
            int intVal = b & 0xff;
            if (intVal < 0x10)
                hexString.append("0");
            hexString.append(Integer.toHexString(intVal));
        }
        return hexString.toString();
    }
    public static Bitmap byteToBitmap(byte[] b) {
        return (b == null || b.length == 0) ? null : BitmapFactory
                .decodeByteArray(b, 0, b.length);
    }
}
