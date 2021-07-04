package in.kriger.newkrigercampus.classes;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CheckSum {

    @FormUrlEncoded
    @POST("generate_checksum")
    Call<ResponseBody> sendUserDetail(
            @Field("cust_id") String cust_id,
            @Field("order_id") String order_id,
            @Field("email") String email,
            @Field("contact") String contact
    );
}
