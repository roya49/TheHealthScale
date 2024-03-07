package Bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sample.thehealthscale.R;
import java.util.List;

public class AdapterForBluetooth extends RecyclerView.Adapter<AdapterForBluetooth.ViewHolder> {

    private List<BluetoothDevice> devices;
    private Context context;

    class ViewHolder extends RecyclerView.ViewHolder{
        View deviceView;
        TextView name;
        TextView address;

        public ViewHolder(View view){
            super(view);
            deviceView = view;
            name = view.findViewById(R.id.bluetooth_name);
            address = view.findViewById(R.id.bluetooth_address);
        }
    }

    public AdapterForBluetooth(List<BluetoothDevice> list){
        devices=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        if(context==null){
            context=parent.getContext();
        }
        final View view= LayoutInflater.from(context).inflate(R.layout.bluetooth_item_layout,parent,false);
        final ViewHolder holder=new ViewHolder(view);

        holder.deviceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                BluetoothDevice device=devices.get(position);
                Toast.makeText(v.getContext(),device.getName()+", "+ device.getAddress(),Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        BluetoothDevice device=devices.get(position);
        if (device.getName() != null)
            holder.name.setText(device.getName());
        else
            holder.name.setText("null");
        holder.address.setText(device.getAddress());
        if(onItemClickListener!=null){
            holder.deviceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(position);
                    //holder.sign.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    //外部接口，长点击事件
    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onClick(int pos);
        void onLongClick(int pos);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
