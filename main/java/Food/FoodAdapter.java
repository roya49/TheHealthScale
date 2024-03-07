package Food;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sample.thehealthscale.R;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<Food> foods;
    private Context context;

    class ViewHolder extends RecyclerView.ViewHolder{

        View mealView;
        TextView foodName;
        TextView kcal;

        public ViewHolder(View view){
            super(view);
            mealView = view;
            foodName = view.findViewById(R.id.text_foodName);
            kcal = view.findViewById(R.id.text_Kcal);
        }
    }

    public FoodAdapter(List<Food> list){
        foods=list;
    }

    @NonNull
    @Override
    public FoodAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        if(context==null){
            context=parent.getContext();
        }
        final View view= LayoutInflater.from(context).inflate(R.layout.food_item_layout,parent,false);
        final FoodAdapter.ViewHolder holder=new FoodAdapter.ViewHolder(view);

//        holder.mealView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position=holder.getAdapterPosition();
//                Food food=foods.get(position);
//                Toast.makeText(v.getContext(),food.getFoodName()+" "+food.getKcal(),Toast.LENGTH_SHORT).show();
//            }
//        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdapter.ViewHolder holder, final int position) {
        Food food=foods.get(position);
        holder.foodName.setText(food.getFoodName());
        holder.kcal.setText(food.getKcal()+"千焦/100g");
        if(onItemClickListener!=null){
            holder.mealView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(position);
                    //holder.sign.setVisibility(View.VISIBLE);
                }
            });

            holder.mealView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onLongClick(position);
                    return false;
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    //外部接口，长点击事件
    private FoodAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onClick(int pos);
        void onLongClick(int pos);
    }

    public void setOnItemClickListener(FoodAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
