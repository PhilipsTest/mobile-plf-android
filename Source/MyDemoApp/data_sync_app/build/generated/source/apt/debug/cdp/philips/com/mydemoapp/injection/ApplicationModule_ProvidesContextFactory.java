package cdp.philips.com.mydemoapp.injection;

import android.content.Context;
import dagger.internal.Factory;
import javax.annotation.Generated;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ApplicationModule_ProvidesContextFactory implements Factory<Context> {
  private final ApplicationModule module;

  public ApplicationModule_ProvidesContextFactory(ApplicationModule module) {  
    assert module != null;
    this.module = module;
  }

  @Override
  public Context get() {  
    Context provided = module.providesContext();
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<Context> create(ApplicationModule module) {  
    return new ApplicationModule_ProvidesContextFactory(module);
  }
}

