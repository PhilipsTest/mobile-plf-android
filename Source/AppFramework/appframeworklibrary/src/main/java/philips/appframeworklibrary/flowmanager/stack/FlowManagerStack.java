package philips.appframeworklibrary.flowmanager.stack;

import java.util.ArrayList;

import philips.appframeworklibrary.flowmanager.base.BaseState;

public class FlowManagerStack extends ArrayList<BaseState> {

    //TODO - need to revise again, document it along with development
    public void push(BaseState baseState) {
        if (!contains(baseState))
            add(baseState);
    }

    // TODO: Deepthi pop operations need not return state
    public BaseState pop() {
        if (size() > 1) {
            remove(size() - 1);
            return get(size() - 1);
        } else if (size() == 1) {
            remove(0);
        }
        return null;
    }

    /**
     * @param state -Pass current state
     * @return - State which is required to update current state
     */
    public BaseState pop(BaseState state) {
        int index = indexOf(state);
        int remove = size() - (index + 1);
        BaseState nextState = null;
        for (int i = 0; i < remove; i++) {
            nextState = pop();
        }
        return nextState;
    }
}
