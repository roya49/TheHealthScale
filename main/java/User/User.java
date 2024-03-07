package User;

import android.util.Log;

public class User {
    private String name;
    private float height;
    private int age;
    private float weight;
    private String sex;
    private float kcal;
    private float BMI;
    private String state;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public float getKcal() {
        return kcal;
    }

    public void setKcal() {
        if (this.sex.equals("男")){
            this.kcal = (float)(Math.ceil((90 + 4.8*height*100 + 13.4*weight - 5.7*age)*1.2));
        }else if (this.sex.equals("女")){
            this.kcal = (float)(Math.ceil(((450 + 3.1*height*100 + 9.2*weight - 4.3*age)*1.2)));
        }
    }

    public float getBMI() {
        return BMI;
    }

    public void setBMI() {
        this.BMI = (float) Math.round((weight/(height*height))*10)/10;
    }

    public String getState() {
        return state;
    }

    public void setState() {
        if (BMI<= 18.5){
            this.state = "过轻";
        }else if (BMI<=24){
            this.state = "正常";
        }else if (BMI<=28){
            this.state = "过重";
        }else if (BMI<=32){
            this.state = "肥胖";
        }else {
            this.state = "非常肥胖";
        }
    }

    public void setAll(String ... str){
        this.name = str[0];
        this.height = Float.parseFloat(str[1]);
        this.age = Integer.parseInt(str[2]);
        this.weight = Float.parseFloat(str[3]);
        this.sex = str[4];
        this.kcal = Float.parseFloat(str[5]);
        this.BMI = Float.parseFloat(str[6]);
    }

    public void showAll(){
        Log.d("user", name);
        Log.d("user", ""+height);
        Log.d("user", ""+age);
        Log.d("user", ""+weight);
        Log.d("user", sex);
        Log.d("user", ""+kcal);
    }

}
