package edu.rit.se.crashavoidance.availableService;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.rit.se.crashavoidance.R;
import edu.rit.se.crashavoidance.main.MainActivity;
import edu.rit.se.wifibuddy.DnsSdService;
import edu.rit.se.wifibuddy.DnsSdTxtRecord;
import edu.rit.se.wifibuddy.WifiDirectHandler;

/**
 *
 */
public class AvailableServicesListViewAdapter extends BaseAdapter {

    private final List<DnsSdService> serviceList;
    private final MainActivity mainActivity;
    private final WifiDirectHandler wifiDirectHandler;
    private final LayoutInflater inflater;

    public AvailableServicesListViewAdapter(final MainActivity mainActivity, final List<DnsSdService> serviceList, WifiDirectHandler wifiDirectHandler) {
        this.mainActivity = mainActivity;
        this.serviceList = serviceList;
        this.wifiDirectHandler = wifiDirectHandler;
        inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DnsSdService service = getItem(position);
        ViewHolder holder;

        // Inflates the template view inside each ListView item
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.service_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        setTexts(service, holder);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onServiceClick(service);
            }
        });

        return convertView;
    }

    private void setTexts(DnsSdService service, ViewHolder holder) {
        holder.connectTextView.setText("Connect");

        setDeviceName(service, holder);

        holder.deviceInfoTextView.setText(getDeviceInfo(service));
    }

    @NonNull
    private String getDeviceInfo(DnsSdService service) {
        Map<String, String> mapTxtRecord;
        String strTxtRecord = "";
        if (wifiDirectHandler != null) {
            DnsSdTxtRecord txtRecord = wifiDirectHandler.getDnsSdTxtRecordMap().get(service.getSrcDevice().deviceAddress);
            if (txtRecord != null) {
                mapTxtRecord = txtRecord.getRecord();
                for (Map.Entry<String, String> record : mapTxtRecord.entrySet()) {
                    strTxtRecord += record.getKey() + ": " + record.getValue() + "\n";
                }
            }
        }
        String status = wifiDirectHandler.deviceStatusToString(wifiDirectHandler.getThisDevice().status);
        return status + "\n" + strTxtRecord;
    }

    private void setDeviceName(DnsSdService service, ViewHolder holder) {
        if(service!= null && service.getSrcDevice()!=null) {
            String sourceDeviceName = service.getSrcDevice().deviceName;
            if (sourceDeviceName.equals("")) {
                sourceDeviceName = "Android Device";
            }
            holder.deviceNameTextView.setText(sourceDeviceName);
        }
    }

    static class ViewHolder {
        @BindView(R.id.deviceName) TextView deviceNameTextView;
        @BindView(R.id.deviceInfo) TextView deviceInfoTextView;
        @BindView(R.id.connect) TextView connectTextView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * Add service to the Services list if it has not already been added
     * @param service Service to be added to list
     * @return false if item was already in the list
     */
    // TODO: the returned boolean of this method is never checked
    public Boolean addUnique(DnsSdService service) {
        if (serviceList.contains(service)) {
            return false;
        } else {
            serviceList.add(service);
            this.notifyDataSetChanged();
            return true;
        }
    }

    @Override
    public int getCount() {
        return serviceList.size();
    }

    @Override
    public DnsSdService getItem(int position) {
        return serviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
