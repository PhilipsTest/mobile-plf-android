
package com.ecs.demotestuapp.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ecs.demotestuapp.R;
import com.ecs.demotestuapp.fragments.AddProductToShoppingCartFragment;
import com.ecs.demotestuapp.fragments.ApplyVoucherFragment;
import com.ecs.demotestuapp.fragments.ConfigureECSFragment;
import com.ecs.demotestuapp.fragments.ConfigureECSToGetConfigurationFragment;
import com.ecs.demotestuapp.fragments.CreateAddressFragment;
import com.ecs.demotestuapp.fragments.CreateAndFetchAddressFragment;
import com.ecs.demotestuapp.fragments.CreateShoppingCartFragment;
import com.ecs.demotestuapp.fragments.DeleteAddressFragment;
import com.ecs.demotestuapp.fragments.DeleteAndFetchAddressFragment;
import com.ecs.demotestuapp.fragments.FetchAppliedVouchersFragment;
import com.ecs.demotestuapp.fragments.FetchDeliveryModesFragment;
import com.ecs.demotestuapp.fragments.FetchOrderDetailForOdersFragment;
import com.ecs.demotestuapp.fragments.FetchOrderDetailForOrderDetailFragment;
import com.ecs.demotestuapp.fragments.FetchOrderDetailFragment;
import com.ecs.demotestuapp.fragments.FetchOrderHistoryFragment;
import com.ecs.demotestuapp.fragments.FetchPaymentsDetailsFragment;
import com.ecs.demotestuapp.fragments.FetchProductDetailsFragment;
import com.ecs.demotestuapp.fragments.FetchProductForCtnFragment;
import com.ecs.demotestuapp.fragments.FetchProductSummariesFragment;
import com.ecs.demotestuapp.fragments.FetchProductsFragment;
import com.ecs.demotestuapp.fragments.FetchRegionsFragment;
import com.ecs.demotestuapp.fragments.FetchRetailersForProductFragment;
import com.ecs.demotestuapp.fragments.FetchRetailersFragment;
import com.ecs.demotestuapp.fragments.FetchSavedAddressesFragment;
import com.ecs.demotestuapp.fragments.FetchShoppingCartFragment;
import com.ecs.demotestuapp.fragments.FetchUserProfileFragment;
import com.ecs.demotestuapp.fragments.HybrisOAthAuthenticationFragment;
import com.ecs.demotestuapp.fragments.HybrisRefreshOAuthFragment;
import com.ecs.demotestuapp.fragments.JanrainRefreshOAuthFragment;
import com.ecs.demotestuapp.fragments.MakePaymentFragment;
import com.ecs.demotestuapp.fragments.PILConfigureECSServiceFragment;
import com.ecs.demotestuapp.fragments.PILFetchProductDetailsFragment;
import com.ecs.demotestuapp.fragments.PILFetchProductForCtnFragment;
import com.ecs.demotestuapp.fragments.PILFetchProductSummariesFragment;
import com.ecs.demotestuapp.fragments.PILFetchProductsFragment;
import com.ecs.demotestuapp.fragments.PILFetchRetailersForProductFragment;
import com.ecs.demotestuapp.fragments.PILFetchRetailersFragment;
import com.ecs.demotestuapp.fragments.RemoveVoucherFragment;
import com.ecs.demotestuapp.fragments.SetAndFetchDeliveryAddressFragment;
import com.ecs.demotestuapp.fragments.SetDeliveryAddressFragment;
import com.ecs.demotestuapp.fragments.SetDeliveryModeFragment;
import com.ecs.demotestuapp.fragments.SetPaymentDetailsFragment;
import com.ecs.demotestuapp.fragments.SubmitOrderFragment;
import com.ecs.demotestuapp.fragments.UpdateAddressFragment;
import com.ecs.demotestuapp.fragments.UpdateAndFetchAddressFragment;
import com.ecs.demotestuapp.fragments.UpdateShoppingCartFragment;
import com.ecs.demotestuapp.jsonmodel.SubgroupItem;


public class InputActivity extends AppCompatActivity {


    private SubgroupItem subgroupItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.input_activity);

        Bundle bundle = getIntent().getExtras();
        subgroupItem = (SubgroupItem) bundle.getSerializable("sub_group");


        loadFragmeent();
    }


    void loadFragmeent() {

        Fragment newFragment = getFragment(subgroupItem.getApiNumber());
        if(newFragment==null) return;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, newFragment).commit();
    }

    private Fragment getFragment(int apiNumber) {

        switch (apiNumber) {

            case 0:
                return null;

            case 1:
                return new ConfigureECSFragment();

            case 2:
                return new ConfigureECSToGetConfigurationFragment();

            case 3:
                return new HybrisOAthAuthenticationFragment();
            case 4:
                return new HybrisRefreshOAuthFragment();


            case 5:
                return new FetchProductsFragment();
            case 6:
                return new FetchProductForCtnFragment();
            case 7:
                return new FetchProductDetailsFragment();

            case 8:
                return new FetchProductSummariesFragment();



            case 9:
                return new FetchShoppingCartFragment();
            case 10:
                return new AddProductToShoppingCartFragment();
            case 11:
                return new CreateShoppingCartFragment();

            case 12:
                return new UpdateShoppingCartFragment();



            case 13:
                return new ApplyVoucherFragment();

            case 14:
                return new FetchAppliedVouchersFragment();

            case 15:
                return new RemoveVoucherFragment();



            case 16:
                return new FetchDeliveryModesFragment();

            case 17:
                return new SetDeliveryModeFragment();




                case 18:
                return new FetchRegionsFragment();


            case 19:
                return new FetchSavedAddressesFragment();
            case 20:
                return new CreateAddressFragment();

            case 21:
                return new CreateAndFetchAddressFragment();
            case 22:
                return new UpdateAddressFragment();

            case 23:
                return new UpdateAndFetchAddressFragment();


            case 24:
                return new SetDeliveryAddressFragment();
            case 25:
                return new SetAndFetchDeliveryAddressFragment();
            case 26:
                return new DeleteAddressFragment();
            case 27:
                return new DeleteAndFetchAddressFragment();


            case 28:
                return new FetchRetailersFragment();
            case 29:
                return new FetchRetailersForProductFragment();

                case 30:
                return new FetchPaymentsDetailsFragment();

            case 31:
                return new SetPaymentDetailsFragment();
            case 32:
                return new MakePaymentFragment();

            case 33:
                return new SubmitOrderFragment();



            case 34:
                return new FetchOrderHistoryFragment();
            case 35:

                return new FetchOrderDetailFragment();
            case 36:
                return new FetchOrderDetailForOrderDetailFragment();
            case 37:

                return new FetchOrderDetailForOdersFragment();
            case 38:
                return new FetchUserProfileFragment();

            case 39:
                return new JanrainRefreshOAuthFragment();

                //Micro services new APIS =============

            case 40:
                return new PILConfigureECSServiceFragment();

            case 45:
                return new PILFetchProductsFragment();

            case 46:
                return new PILFetchProductForCtnFragment();
            case 47:
                return new PILFetchProductDetailsFragment();

            case 48:
                return new PILFetchProductSummariesFragment();
            case 68:
                return new PILFetchRetailersFragment();
            case 69:
                return new PILFetchRetailersForProductFragment();

            default:
                return null;
        }
    }


}
