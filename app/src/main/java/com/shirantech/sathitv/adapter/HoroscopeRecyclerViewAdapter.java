package com.shirantech.sathitv.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.model.response.HoroscopeResponse;
import com.shirantech.sathitv.widget.CustomFontBoldTextView;
import com.shirantech.sathitv.widget.CustomFontTextView;

import java.util.List;


public class HoroscopeRecyclerViewAdapter extends RecyclerView.Adapter<HoroscopeRecyclerViewAdapter.ViewHolder> {

    private final List<HoroscopeResponse.Horoscope> mValues;
//    private final HoroscopeFragment.OnListFragmentInteractionListener mListener;

    public HoroscopeRecyclerViewAdapter(List<HoroscopeResponse.Horoscope> items) {
        mValues = items;
//        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horoscope_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.textViewDescription.setText(holder.mItem.getDescription());
//        holder.textViewDescription.setText("The underground origins series :MMA has changed a lot in the past te years.For Example, world class wresthlers can no longer knee to head on the ground.That's a good thing.");

       /*  holder.textViewTitle.setText(holder.mItem.getTitle());
        AppLog.showLog("Get the horoscope title",holder.mItem.getTitle());
        String currentPhoto = holder.mItem.getImage();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(
                Uri.parse(currentPhoto))
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(holder.mImageView.getController())
                .build();
        holder.imageView.setController(cont roller);*/
        switch (position){
            case 0:
                holder.textViewTitle.setText(R.string.horoscope_aries);
                holder.textViewDate.setText(R.string.date_aries);
                holder.imageView.setBackgroundResource(R.drawable.ic_aries);
                break;
            case 1:
                holder.textViewTitle.setText(R.string.horoscope_taurus);
                holder.textViewDate.setText(R.string.date_taurus);
                holder.imageView.setBackgroundResource(R.drawable.ic_taurus);
                break;
            case 2:
                holder.textViewTitle.setText(R.string.horoscope_gemini);
                holder.textViewDate.setText(R.string.date_gemini);
                holder.imageView.setBackgroundResource(R.drawable.ic_gemini);
                break;
            case 3:
                holder.textViewTitle.setText(R.string.horoscope_cancer);
                holder.textViewDate.setText(R.string.date_cancer);
                holder.imageView.setBackgroundResource(R.drawable.ic_cancer);
                break;
            case 4:
                holder.textViewTitle.setText(R.string.horoscope_leo);
                holder.textViewDate.setText(R.string.date_leo);
                holder.imageView.setBackgroundResource(R.drawable.ic_leo);
                break;
            case 5:
                holder.textViewTitle.setText(R.string.horoscope_virgo);
                holder.textViewDate.setText(R.string.date_virgo);
                holder.imageView.setBackgroundResource(R.drawable.ic_virgo);
                break;
            case 6:
                holder.textViewTitle.setText(R.string.horoscope_libra);
                holder.textViewDate.setText(R.string.date_libra);
                holder.imageView.setBackgroundResource(R.drawable.ic_libra);
                break;
            case 7:
                holder.textViewTitle.setText(R.string.horoscope_scorpio);
                holder.textViewDate.setText(R.string.date_scorpio);
                holder.imageView.setBackgroundResource(R.drawable.ic_scorpio);
                break;
            case 8:
                holder.textViewTitle.setText(R.string.horoscope_sagittarius);
                holder.textViewDate.setText(R.string.date_sagittarius);
                holder.imageView.setBackgroundResource(R.drawable.ic_saggitarius);
                break;
            case 9:
                holder.textViewTitle.setText(R.string.horoscope_capricorn);
                holder.textViewDate.setText(R.string.date_capricorn);
                holder.imageView.setBackgroundResource(R.drawable.ic_capricon);
                break;
            case 10:
                holder.textViewTitle.setText(R.string.horoscope_aquarius);
                holder.textViewDate.setText(R.string.date_aquarius);
                holder.imageView.setBackgroundResource(R.drawable.ic_aquarious);
                break;
            case 11:
                holder.textViewTitle.setText(R.string.horoscope_pisces);
                holder.textViewDate.setText(R.string.date_pisces);
                holder.imageView.setBackgroundResource(R.drawable.ic_pisces);
                break;
            }

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final SimpleDraweeView imageView;
        public final CustomFontTextView textViewDate, textViewDescription;
        public final CustomFontBoldTextView textViewTitle;
        public HoroscopeResponse.Horoscope mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            imageView = (SimpleDraweeView) view.findViewById(R.id.imageViewHoroscope);
            textViewTitle = (CustomFontBoldTextView) view.findViewById(R.id.textViewHorscopeSubject);
            textViewDate = (CustomFontTextView) view.findViewById(R.id.textViewHorscopeDate);
            textViewDescription = (CustomFontTextView) view.findViewById(R.id.textviewHoroscopeDescription);
        }

    }
}
