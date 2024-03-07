package lk.javainstitute.petpulse_v2.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import lk.javainstitute.petpulse_v2.vet.VetLogInFragment;
import lk.javainstitute.petpulse_v2.vet.VetSignUpFragment;

public class ViewPageAdapter_Vet extends FragmentStateAdapter {

    public ViewPageAdapter_Vet(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new VetSignUpFragment();
        }
        return new VetLogInFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
