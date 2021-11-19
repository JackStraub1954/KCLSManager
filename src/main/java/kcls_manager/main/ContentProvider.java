package kcls_manager.main;

public abstract class ContentProvider implements Iterable<Object[]>
{
    public abstract Object[]    getHeaders();
    public abstract String      getDialogTitle();
}
