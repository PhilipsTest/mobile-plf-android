package uappadaptor;

/**
 * Created by philips on 11/16/17.
 */

public abstract class UserInterface implements DataInterface {


    @Override
    public abstract DataModel getData(DataModelType dataModelType);
}
