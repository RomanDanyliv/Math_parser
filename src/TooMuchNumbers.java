/**
 * Created by Neyro on 11.08.2017.
 */
public class TooMuchNumbers extends Exception {
    @Override
    public String getMessage() {
        return "Занадто великі цифри, я не можу такого порахувати";
    }
}
