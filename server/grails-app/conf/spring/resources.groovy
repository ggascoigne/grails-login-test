import com.wyrdrune.UserPasswordEncoderListener

// Place your Spring DSL code here
beans = {
  userPasswordEncoderListener(UserPasswordEncoderListener, ref('hibernateDatastore'))
}
