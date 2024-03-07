package Food;

public class Food {

    private String foodName;
    private float kcal;
    private float protein;
    private float fat;
    private float carbohydrate;
    private int weight;

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public float getKcal() {
        return kcal;
    }

    public void setKcal(float kcal) {
        this.kcal = kcal;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public float getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(float carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setAll(String ... foodInfo){
        this.foodName = foodInfo[0];
        this.kcal = Float.parseFloat(foodInfo[1]);
        this.protein = Float.parseFloat(foodInfo[2]);
        this.fat = Float.parseFloat(foodInfo[3]);
        this.carbohydrate = Float.parseFloat(foodInfo[4]);
    }

}
