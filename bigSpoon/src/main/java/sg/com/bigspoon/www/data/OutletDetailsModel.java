package sg.com.bigspoon.www.data;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Comparator;

public class OutletDetailsModel {
    public DishModel[] dishes;
    public CategoryModel[] categoriesDetails;
    public CategoryOrder[] categoriesOrder;
    public TableModel[] tables;
    public StoryModel[] storys;

    public int restaurant;
    public String imgURL;
    public String name;
    public String address;

    public Integer[] storySequence;

    @SerializedName("phone")
    public String phoneNumber;

    @SerializedName("opening")
    public String operatingHours;

    @SerializedName("default_dish_photo")
    public String defaultDishPhoto;

    @SerializedName("discount")
    public String promotionalText;

    @SerializedName("water_popup_text")
    public String waterText;

    @SerializedName("bill_popup_text")
    public String billText;

    @SerializedName("id")
    public int outletID;

    public double lat;

    public double lng;
    @SerializedName("gst")
    public double gstRate;

    @SerializedName("scr")
    public double serviceChargeRate;

    @SerializedName("location_diameter")
    public double locationThreshold;

    @SerializedName("is_active")
    public boolean isActive;

    @SerializedName("is_by_default_photo_menu")
    public boolean isDefaultPhotoMenu;

    @SerializedName("request_for_water_enabled")
    public boolean isWaterEnabled;

    @SerializedName("ask_for_bill_enabled")
    public boolean isBillEnabled;

    @SerializedName("clear_sent_orders_interval")
    public int clearPastOrdersInterval;

    @SerializedName("story_display_interval")
    public int storyDisplayInterval;

    public static OutletDetailsModel getInstanceFromJsonObject(JsonObject json) {
        final Gson gson = new Gson();
        final OutletDetailsModel outletDetails = (OutletDetailsModel) gson.fromJson(json, OutletDetailsModel.class);
        outletDetails.tables = gson.fromJson(json.get("tables"), TableModel[].class);
        outletDetails.categoriesDetails = gson.fromJson(json.get("categories"), CategoryModel[].class);
        outletDetails.categoriesOrder = gson.fromJson(json.get("categories_order"), CategoryOrder[].class);
        outletDetails.dishes = gson.fromJson(json.get("dishes"), DishModel[].class);

        final JsonParser parser = new JsonParser();
        try {
            outletDetails.storySequence = gson.fromJson(json.get("story_json_array").getAsString(), Integer[].class);
        } catch (Exception exp) {
            Crashlytics.log(exp.toString());
        }

        for (int i = 0, len = outletDetails.dishes.length; i < len; i++) {
            if (outletDetails.dishes[i].customizable) {
                JsonObject modifierJson = (JsonObject) parser.parse(outletDetails.dishes[i].modifierJsonString);
                outletDetails.dishes[i].modifier = gson.fromJson(modifierJson, Modifer.class);
            }
        }

        return outletDetails;
    }

    public JsonObject toJsonObject() {
        final Gson gson = new Gson();
        final JsonObject result = (JsonObject) gson.toJsonTree(this, OutletDetailsModel.class);

        result.add("tables", gson.toJsonTree(this.tables, TableModel[].class));
        result.add("categories", gson.toJsonTree(this.categoriesDetails, CategoryModel[].class));
        result.add("categories_order", gson.toJsonTree(this.categoriesOrder, CategoryOrder[].class));
        result.add("dishes", gson.toJsonTree(this.dishes, DishModel[].class));

        try {
            result.add("story_json_array", gson.toJsonTree(this.storySequence, Integer[].class));
        } catch (Exception exp) {
            Crashlytics.log(exp.toString());
        }

        return result;
    }

    public DishModel getDishWithId(int dishId) {
        for (int i = 0, len = dishes.length; i < len; i++) {
            if (dishes[i].id == dishId) {
                return dishes[i];
            }
        }

        return null;
    }

    public DishModel getDishWithName(String dishName) {
        for (int i = 0, len = dishes.length; i < len; i++) {
            if (dishes[i].name.equals(dishName)) {
                return dishes[i];
            }
        }

        return null;
    }

    public CategoryModel getCategoryForDishWithId(int dishId) {
        final DishModel dish = getDishWithId(dishId);
        if (dish == null) return null;

        for (int i = 0; i < categoriesDetails.length; i++) {
            if (dish.categories[0].id == categoriesDetails[i].id) {
                return categoriesDetails[i];
            }
        }

        return null;
    }

    public int getCategoryIdForDishWithId(int dishId) {
        final CategoryModel category = getCategoryForDishWithId(dishId);
        if (category == null) return -1;
        else return category.id;
    }

    public int getCategoryPositionForCategoryWithId(int categoryId) {
        for (int i = 0; i < categoriesOrder.length; i++) {
            if (categoriesOrder[i].categoryId == categoryId) {
                return categoriesOrder[i].position;
            }
        }

        return -1;
    }

    public int getCategoryPositionWithDishId(int dishId) {
        final int categoryId = getCategoryIdForDishWithId(dishId);
        return getCategoryPositionForCategoryWithId(categoryId);
    }

    public boolean hasTable(int tabletId) {
        for (TableModel table : this.tables) {
            if (table.id == tabletId && tabletId != -1) {
                return true;
            }
        }
        return false;
    }

    public void sortOrder() {
        Arrays.sort(categoriesDetails, new Comparator<CategoryModel>() {
            public int compare(CategoryModel cat1, CategoryModel cat2) {
                int cat1Index = 0;
                int cat2Index = 0;
                for (int i = 0, len = categoriesOrder.length; i < len; i++) {
                    if (categoriesOrder[i].categoryId == cat1.id) {
                        cat1Index = categoriesOrder[i].position;
                    }

                    if (categoriesOrder[i].categoryId == cat2.id) {
                        cat2Index = categoriesOrder[i].position;
                    }
                }
                return cat1Index - cat2Index;
            }
        });
    }
}
