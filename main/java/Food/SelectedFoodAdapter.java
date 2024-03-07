package Food;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.thehealthscale.R;

import java.util.List;

import Meal.AddMeal;

public class SelectedFoodAdapter extends RecyclerView.Adapter<SelectedFoodAdapter.ViewHolder> {

    private List<Food> foods;
    private Context context;

    class ViewHolder extends RecyclerView.ViewHolder{

        View selectedView;
        TextView foodName;
        TextView kcal;
        LinearLayout delete;

        public ViewHolder(View view){
            super(view);
            selectedView = view;
            foodName = view.findViewById(R.id.text_selectedFoodName);
            kcal = view.findViewById(R.id.text_selectedKcal);
            delete = view.findViewById(R.id.delete_img);
        }
    }

    public SelectedFoodAdapter(List<Food> list){
        foods=list;
    }

    @NonNull
    @Override
    public SelectedFoodAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        if(context==null){
            context=parent.getContext();
        }
        final View view= LayoutInflater.from(context).inflate(R.layout.selected_item_layout,parent,false);
        final SelectedFoodAdapter.ViewHolder holder=new SelectedFoodAdapter.ViewHolder(view);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                AddMeal.kcal -= Math.ceil(((float)foods.get(position).getWeight()/100)*foods.get(position).getKcal());
                foods.remove(position);
                AddMeal.selected_pos -= 1;
                //刷新Adapter
                notifyDataSetChanged();
                AddMeal.totalKcal.setText(AddMeal.kcal+"千卡");
                if (foods.size() == 0){
                    AddMeal.noSelected.setVisibility(View.VISIBLE);
                    AddMeal.selected.setEnabled(false);
                    AddMeal.submit.setEnabled(false);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedFoodAdapter.ViewHolder holder, final int position) {
        Food food=foods.get(position);
        holder.foodName.setText(food.getFoodName());
        holder.kcal.setText(food.getKcal()+"千焦/100g");

        if(onItemClickListener!=null) {
            holder.selectedView.setOnClickListener(new View.OnClickListener() {
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
        return foods.size();
    }

    //外部接口，长点击事件
    private SelectedFoodAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onClick(int pos);
        void onLongClick(int pos);
    }

    public void setOnItemClickListener(SelectedFoodAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
