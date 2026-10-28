public class Validator {
    public static boolean validateText(String text)
{
    if(text!=null && text.trim().length() != 0)
    {
        return true;
    }
    return false;
}

public static boolean validateNumber(int num)
{
    if(num > 0)
    {
        return true;
    }
    return false;
}
}