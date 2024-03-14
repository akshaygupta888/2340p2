package com.spotifyapp.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotifyapp.R;
import com.spotifyapp.databinding.FragmentSignUpBinding;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private LoginViewModel loginViewModel;
    private FragmentSignUpBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText emailEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button signUpButton = binding.signUp;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            signUpButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getEmailError() != null) {
                emailEditText.setError(getString(loginFormState.getEmailError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                signUp(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        signUpButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            signUp(emailEditText.getText().toString(),
                    passwordEditText.getText().toString());
        });
    }

    private void signUp(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            createFirestoreDocument(user);
                        }
                    } else {
                        showSignUpFailed(R.string.sign_up_failed);
                    }
                });
    }

    private void createFirestoreDocument(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", user.getEmail());
        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(unused -> updateUiWithUser(user.getEmail()))
                .addOnFailureListener(e -> showSignUpFailed(R.string.sign_up_failed));
    }

    private void updateUiWithUser(String email) {
        String welcome = getString(R.string.welcome) + email;
        Toast.makeText(requireContext(), welcome, Toast.LENGTH_LONG).show();
        // TODO: Navigate to the next screen or perform necessary actions after sign-up.
    }

    private void showSignUpFailed(@StringRes Integer errorString) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
