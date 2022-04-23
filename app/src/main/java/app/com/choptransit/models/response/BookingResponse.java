package app.com.choptransit.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BookingResponse extends BaseResponse {
    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public class Data {

        @SerializedName("bookings")
        private ArrayList<BookingResponseData> bookingsList;

        public ArrayList<BookingResponseData> getBookingsList() {
            return bookingsList;
        }

        public void setBookingsList(ArrayList<BookingResponseData> bookingsList) {
            this.bookingsList = bookingsList;
        }
    }
}
