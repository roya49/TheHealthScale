package Meal;

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
import Bluetooth.AdapterForBluetooth;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {

private List<Meal> meals;
private Context context;

class ViewHolder extends RecyclerView.ViewHolder{

    View mealView;
    TextView date;
    TextView mealDetail;

    public ViewHolder(View view){
        super(view);
        mealView = view;
        date = view.findViewById(R.id.meal_date);
        mealDetail = view.findViewById(R.id.meal_text);
    }
}

    public MealAdapter(List<Meal> list){
        meals=list;
    }

    @NonNull
    @Override
    public MealAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        if(context==null){
            context=parent.getContext();
        }

        final View view= LayoutInflater.from(context).inflate(R.layout.meal_item_layout,parent,false);
        final MealAdapter.ViewHolder holder=new MealAdapter.ViewHolder(view);

        holder.mealView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Meal meal=meals.get(position);
                Toast.makeText(v.getContext(),meal.getDate()+" "+meal.getMeal(),Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MealAdapter.ViewHolder holder, final int position) {
        Meal meal=meals.get(position);
        holder.date.setText(meal.getDate());
        holder.mealDetail.setText(meal.getMeal());
        if(onItemClickListener!=null){
            holder.mealView.setOnClickListener(new View.OnClickListener() {
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
        return meals.size();
    }

    //外部接口，长点击事件
    private AdapterForBluetooth.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
    void onClick(int pos);
    void onLongClick(int pos);
}

    public void setOnItemClickListener(AdapterForBluetooth.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}

