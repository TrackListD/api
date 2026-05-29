package tracklistd.api.Entity;

import tracklistd.api.Entity.Enums.ModerationStatus;
import tracklistd.api.Entity.Interfaces.Reportable;

public class User implements Reportable{

    String name;
    public String getName(){
        return name;
    }
    @Override
    public String getContentReported() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getContentReported'");
    }
    @Override
    public ModerationStatus getStatusModeration() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getStatusModeration'");
    }
    @Override
    public Reportable getTarget() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTarget'");
    }
}
