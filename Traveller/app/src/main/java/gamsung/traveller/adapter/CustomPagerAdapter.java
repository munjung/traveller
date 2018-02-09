package gamsung.traveller.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gamsung.traveller.R;
import gamsung.traveller.activity.CustomGalleryActivity;
import gamsung.traveller.activity.EditLocationActivity;

/**
 * Created by Jiwon on 2018-01-30.
 */

public class CustomPagerAdapter extends android.support.v4.view.PagerAdapter implements  View.OnClickListener{

    LayoutInflater inflater;
    public static ImageButton addImgBtn;
    Context context;
    String imgPath;
    ImageView img;
    //Bitmap imgBitmap;


    List<String> imgPathList = new ArrayList<>();

    public CustomPagerAdapter(LayoutInflater inflater, Context context) {

        // TODO Auto-generated constructor stub
        //전달 받은 LayoutInflater를 멤버변수로 전달
        this.inflater=inflater;
        this.context = context;
       // this.imgPath = imgPath;
    }

   /* public CustomPagerAdapter(LayoutInflater inflater, Context context) {

        this(inflater, context, null);

    }*/

    //PagerAdapter가 가지고 잇는 View의 개수를 리턴
    //보통 보여줘야하는 이미지 배열 데이터의 길이를 리턴

    @Override

    public int getCount() {

        // TODO Auto-generated method stub
        return 10; //이미지 개수 리턴(그림이 10개라서 10을 리턴)

    }


    //ViewPager가 현재 보여질 Item(View객체)를 생성할 필요가 있는 때 자동으로 호출
    //쉽게 말해, 스크롤을 통해 현재 보여져야 하는 View를 만들어냄.
    //첫번째 파라미터 : ViewPager
    //두번째 파라미터 : ViewPager가 보여줄 View의 위치(가장 처음부터 0,1,2,3...)

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {

        // TODO Auto-generated method stub

        View view=null;

        //새로운 View 객체를 Layoutinflater를 이용해서 생성
        //만들어질 View의 설계는 res폴더>>layout폴더>>viewpater_childview.xml 레이아웃 파일 사용
        view= inflater.inflate(R.layout.viewpager_childview, null);

        //만들어진 View안에 있는 ImageView 객체 참조
        //위에서 inflated 되어 만들어진 view로부터 findViewById()를 해야 하는 것에 주의.
        img= (ImageView)view.findViewById(R.id.img_viewpager_childimage);
        addImgBtn = (ImageButton)view.findViewById(R.id.addImgBtn);
        //사진 추가 버튼 클릭시
        context = container.getContext();
        addImgBtn = (ImageButton)view.findViewById(R.id.addImgBtn);

        addImgBtn.setOnClickListener(this);

        if(imgPathList.size() > position) {

//           img.setImageResource(R.drawable.cheeze3);

            Glide.with(context)
                    .load(imgPathList.get(position))
                    .into(img);

//            Glide.with(context).load(imgPathList.get(position)).into(img);
            Log.d("intput img", position + " : " + imgPathList.get(position));

        }

        Log.d("position id : ", "" + position);

        //ImageView에 현재 position 번째에 해당하는 이미지를 보여주기 위한 작업
        //현재 position에 해당하는 이미지를 setting
//        img.setImageResource(R.drawable.gametitle_01+position);
      //  img.setImageURI(EditLocationActivity.imgBitmap);
        img.setAdjustViewBounds(true);
       // img.setImageBitmap(EditLocationActivity.imgBitmap);
          Toast.makeText(context, getImgPath()+"경로임",Toast.LENGTH_SHORT).show();
//        Log.d("경로", imgPath.toString());
      //  img.setImageBitmap(EditLocationActivity.imgPath.);
//        //ViewPager에 만들어 낸 View 추가
        container.addView(view);

        //Image가 세팅된 View를 리턴
        return view;

    }


    //화면에 보이지 않은 View는파쾨를 해서 메모리를 관리함.
    //첫번째 파라미터 : ViewPager
    //두번째 파라미터 : 파괴될 View의 인덱스(가장 처음부터 0,1,2,3...)
    //세번째 파라미터 : 파괴될 객체(더 이상 보이지 않은 View 객체)
    @Override

    public void destroyItem(ViewGroup container, int position, Object object) {

        // TODO Auto-generated method stub
       // Toast.makeText(context, imgPath+"경로로로ㅗ로ㅗ222",Toast.LENGTH_SHORT).show();
        //ViewPager에서 보이지 않는 View는 제거
        //세번째 파라미터가 View 객체 이지만 데이터 타입이 Object여서 형변환 실시

        container.removeView((View)object);

    }

    //instantiateItem() 메소드에서 리턴된 Ojbect가 View가  맞는지 확인하는 메소드
    @Override

    public boolean isViewFromObject(View v, Object obj) {
        // TODO Auto-generated method stub
       // Toast.makeText(context, imgPath+"경로로로ㅗ로ㅗ333",Toast.LENGTH_SHORT).show();
        return v==obj;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    @Override
    public void onClick(View view) {
        //갤러리 이동
        Intent intent = new Intent(context, CustomGalleryActivity.class);
        ((Activity) context).startActivityForResult(intent, 1);
      //  Toast.makeText(context, EditLocationActivity.imgPath+"이미지 경로임", Toast.LENGTH_SHORT).show();
    }

    public void setImgPathArr(List<String> imgPathList){
        this.imgPathList.clear();
        this.imgPathList.addAll(imgPathList);
    }

    public void setImgPath(String imgPath){
        this.imgPath = imgPath;
        this.imgPathList.clear();
        this.imgPathList.add(imgPath);
    }

    public String getImgPath(){
        return imgPath;
    }



}

