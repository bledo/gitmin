package co.bledo.gitmin.db;

import java.util.List;

public interface Storage {
	public void userSave(User user) throws DbException;
	public User userFetch(String username) throws DbException, NotFoundException;
	public List<Repo> repositoryFetchAll() throws DbException;
}
