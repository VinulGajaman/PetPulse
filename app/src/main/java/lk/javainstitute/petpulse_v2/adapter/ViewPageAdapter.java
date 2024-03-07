package lk.javainstitute.petpulse_v2.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import lk.javainstitute.petpulse_v2.petOwner.PetOwner_LogInFragment;
import lk.javainstitute.petpulse_v2.petOwner.PetOwner_SignUpFragment;

public class ViewPageAdapter extends FragmentStateAdapter {
    public ViewPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1){
            return new PetOwner_SignUpFragment();
        }
        return new PetOwner_LogInFragment();
    }
    @Override
    public int getItemCount() {
        return 2;
    }


}


