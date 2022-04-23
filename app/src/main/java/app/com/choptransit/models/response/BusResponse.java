package app.com.choptransit.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class BusResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        @SerializedName("buses")
        private ArrayList<BusData> busesList;

        public ArrayList<BusData> getBusesList() {
            return busesList;
        }

        public void setBusesList(ArrayList<BusData> busesList) {
            this.busesList = busesList;
        }
    }

    public class BusData implements Serializable {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("busName")
        @Expose
        private String busName;
        @SerializedName("busRegNo")
        @Expose
        private String busRegNo;
        @SerializedName("busType")
        @Expose
        private String busType;
        @SerializedName("busColor")
        @Expose
        private String busColor;
        @SerializedName("vacancy")
        @Expose
        private String vacancy;
        @SerializedName("assignedTo")
        @Expose
        private String assignedTo;
        @SerializedName("driver")
        @Expose
        private DriverData driver;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBusName() {
            return busName;
        }

        public void setBusName(String busName) {
            this.busName = busName;
        }

        public String getBusRegNo() {
            return busRegNo;
        }

        public void setBusRegNo(String busRegNo) {
            this.busRegNo = busRegNo;
        }

        public String getBusType() {
            return busType;
        }

        public void setBusType(String busType) {
            this.busType = busType;
        }

        public String getBusColor() {
            return busColor;
        }

        public void setBusColor(String busColor) {
            this.busColor = busColor;
        }

        public String getAssignedTo() {
            return assignedTo;
        }

        public void setAssignedTo(String assignedTo) {
            this.assignedTo = assignedTo;
        }

        public DriverData getDriver() {
            return driver;
        }

        public void setDriver(DriverData driver) {
            this.driver = driver;
        }

        public String getVacancy() {
            return vacancy;
        }

        public void setVacancy(String vacancy) {
            this.vacancy = vacancy;
        }
    }
}





