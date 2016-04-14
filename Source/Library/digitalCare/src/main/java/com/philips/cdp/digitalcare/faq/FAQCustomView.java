package com.philips.cdp.digitalcare.faq;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.faq.listeners.FaqCallback;
import com.philips.cdp.digitalcare.util.DigiCareLogger;
import com.philips.cdp.prxclient.datamodels.support.Chapter;
import com.philips.cdp.prxclient.datamodels.support.Data;
import com.philips.cdp.prxclient.datamodels.support.Item;
import com.philips.cdp.prxclient.datamodels.support.RichText;
import com.philips.cdp.prxclient.datamodels.support.RichTexts;
import com.philips.cdp.prxclient.datamodels.support.SupportModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by naveen@philips.com on 05-Apr-16.
 */
public class FAQCustomView {

    private static final String TAG = FAQCustomView.class.getSimpleName();
    private ArrayList<View> mSubQuestionViewList = null;
    private ArrayList<View> mQuestionViewList = null;
    private Context mContext = null;
    private SupportModel mSupportModel = null;
    private FaqFragment mFaqFragment = null;
    private FaqCallback mCallback = null;
    private float mDensity;

    public FAQCustomView(Context context, SupportModel supportModel, FaqCallback callback) {
        this.mContext = context;
        this.mSupportModel = supportModel;
        this.mCallback = callback;
        mDensity = context.getResources().getDisplayMetrics().density;
        mSubQuestionViewList = new ArrayList<View>();
        mQuestionViewList = new ArrayList<View>();
    }

    protected View init() {
        FaqViewContainer faqViewContainer = new FaqViewContainer().invoke();
        LinearLayout container = faqViewContainer.getContainer();
        ScrollView scrollView = faqViewContainer.getScrollView();

        LinearLayout questionsView = new LinearLayout(mContext);
        questionsView.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams questionsViewparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        questionsView.setLayoutParams(questionsViewparams);


        LinkedHashMap linkedHashMap = getFaqData();

        Set set = linkedHashMap.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();
            List<FaqQuestionModel> value = (List<FaqQuestionModel>) entry.getValue();

            DigiCareLogger.v(TAG, "Question Categories : " + key + " & Value : " + value.size());

            LinearLayout mDividerLine = getDividerLayout(ContextCompat.getColor(mContext, R.color.transparent));

            questionsView.addView(mDividerLine);
            View parent = getQuestionTypeView(key.toString() + " (" + value.size() + ")");
            mQuestionViewList.add(parent);
            questionsView.addView(parent);

            LinearLayout subQuestionView = new LinearLayout(mContext);
            subQuestionView.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams subQuestionViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            subQuestionView.setLayoutParams(subQuestionViewParams);

            subQuestionView.addView(addSubQuestion(value));
            questionsView.addView(subQuestionView);
        }
        container.addView(questionsView);

        scrollView.addView(container);

        return scrollView;

    }

    @NonNull
    private LinearLayout getDividerLayout(int color) {
        LinearLayout mDividerLine = new LinearLayout(mContext);
        LinearLayout.LayoutParams mDividerLineaParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (int) (mContext.getResources()
                .getDimension(R.dimen.faq_top_margin) * mDensity));
        mDividerLine.setBackgroundColor(color);
        mDividerLine.setLayoutParams(mDividerLineaParams);
        return mDividerLine;
    }

    private View addSubQuestion(List<FaqQuestionModel> value) {

        LinearLayout view = new LinearLayout(mContext);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setLayoutParams(viewParams);

        for (int i = 0; i < value.size(); i++) {

            // for(final FaqQuestionModel faqQuestionModel : value){
            final FaqQuestionModel faqQuestionModel = value.get(i);
            View child1 = getQuestionView(faqQuestionModel.getQuestion());
            child1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mCallback.onFaqQuestionClicked(faqQuestionModel.getAnswer());


                }
            });
            if (1 != value.size())
                view.addView(getDividerLayout(Color.RED));
            view.addView(child1);

        }

        mSubQuestionViewList.add(view);
        return view;

    }

    private View getQuestionView(String question) {
        RelativeLayout questionView = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams questionLayoutParams = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT, (int) (mContext.getResources()
                        .getDimension(R.dimen.support_btn_height) * mDensity));
        questionView.setBackgroundColor(Color.GREEN);
        // questionView.setBackgroundResource(R.drawable.uikit_grad_green_bright_to_light);

        TextView questionTextView = new TextView(mContext);
        RelativeLayout.LayoutParams questionTextparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        questionTextparams.addRule(RelativeLayout.CENTER_IN_PARENT);

        questionTextView.setText(question);
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "digitalcarefonts/CentraleSans-Book.otf");
        questionTextView.setTypeface(typeface);
        questionTextView.setTextColor(ContextCompat.getColor(mContext, R.color.button_background));
        int padding = (int) (mContext.getResources()
                .getDimension(R.dimen.marginTopButton) * mDensity);
        questionTextView.setPadding(padding, 0, padding, 0);
        questionTextView.setLayoutParams(questionTextparams);


        questionView.setLayoutParams(questionLayoutParams);

        questionView.addView(questionTextView);

        return questionView;
    }

    private View getQuestionTypeView(String questionType) {
        final RelativeLayout questionTypeView = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams questionTypeParams = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT, (int) (mContext.getResources()
                        .getDimension(R.dimen.support_btn_height) * mDensity));
        questionTypeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DigiCareLogger.d(TAG, " Count : " + questionTypeView.getChildCount());
            }
        });

        // questionTypeView.setBackgroundResource(R.drawable.uikit_grad_blue_bright_to_light);
        int topMarginOfQuestionType = (int) (mContext.getResources()
                .getDimension(R.dimen.err_alert_width) * mDensity);
        DigiCareLogger.d("FaqDeta", " : " + topMarginOfQuestionType);
        //   questionTypeView.setBackgroundColor(Color.parseColor("#C8E7EE"));
        questionTypeView.setBackgroundColor(Color.RED);
        questionTypeParams.setMargins(0, topMarginOfQuestionType, 0, 0);

        TextView headerText = new TextView(mContext);
        headerText.setText(questionType);
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "digitalcarefonts/CentraleSans-Bold.otf");
        headerText.setTypeface(typeface);
        // headerText.setTextColor(mContext.getResources().getColor(R.color.button_background, null));
        headerText.setTextColor(ContextCompat.getColor(mContext, R.color.button_background));
        RelativeLayout.LayoutParams headerTextParams = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        headerTextParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        headerTextParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        int padding = (int) (mContext.getResources()
                .getDimension(R.dimen.marginTopButton) * mDensity);
        headerText.setPadding(padding, 0, 0, 0);

        ImageView arrowImage = new ImageView(mContext);
        RelativeLayout.LayoutParams arrowImageParams = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        arrowImageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        arrowImageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        //   arrowImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.uikit_arrow_up, null));
        arrowImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.uikit_arrow_up));
        arrowImage.setPadding(0, 0, padding, 0);


        questionTypeView.setLayoutParams(questionTypeParams);
        headerText.setLayoutParams(headerTextParams);
        arrowImage.setLayoutParams(arrowImageParams);

        questionTypeView.addView(headerText);
        questionTypeView.addView(arrowImage);

        return questionTypeView;
    }

    private LinkedHashMap getFaqData() {
        if (mSupportModel != null) {
            DigiCareLogger.d(TAG, "Support Model is Not null");
            Data supportData = mSupportModel.getData();
            RichTexts richTexts = supportData.getRichTexts();
            List<RichText> richText = richTexts.getRichText();
            LinkedHashMap map = new LinkedHashMap();

            for (RichText faq : richText) {
                String questionCategory = null;
                List<FaqQuestionModel> faqQuestionModelList = new ArrayList<FaqQuestionModel>();
                String supportType = faq.getType();
                if (supportType.equalsIgnoreCase("FAQ")) {
                    Chapter chapter = faq.getChapter();
                    questionCategory = chapter.getName();
                    DigiCareLogger.v(TAG, "Question Category : " + questionCategory);
                    if (questionCategory != null) {
                        List<Item> questionsList = faq.getItem();
                        for (Item item : questionsList) {
                            FaqQuestionModel faqQuestionModel = new FaqQuestionModel();
                            String question = null;
                            String answer = null;
                            question = item.getHead();
                            answer = item.getAsset();
                            faqQuestionModel.setQuestion(question);
                            faqQuestionModel.setAnsmer(answer);
                            faqQuestionModelList.add(faqQuestionModel);
                        }
                    }
                    map.put(questionCategory, faqQuestionModelList);
                }
            }
            return map;

        } else {
            DigiCareLogger.d(TAG, "Support Model is null");
            return null;
        }
    }


    private void disPlayQuestionCategories(LinkedHashMap linkedHashMap) {
        Set set = linkedHashMap.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();
            List<FaqQuestionModel> value = (List<FaqQuestionModel>) entry.getValue();

            DigiCareLogger.v(TAG, "Question Categories : " + key + " & Value : " + value.size());
        }
    }


    private class FaqViewContainer {
        private ScrollView scrollView;
        private LinearLayout container;

        public ScrollView getScrollView() {
            return scrollView;
        }

        public LinearLayout getContainer() {
            return container;
        }

        public FaqViewContainer invoke() {
            scrollView = new ScrollView(mContext);
            RelativeLayout.LayoutParams scrollViewLayoutParams = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            scrollView.setLayoutParams(scrollViewLayoutParams);

            container = new LinearLayout(mContext);
            container.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams containerparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            container.setLayoutParams(containerparams);
            return this;
        }
    }
}
