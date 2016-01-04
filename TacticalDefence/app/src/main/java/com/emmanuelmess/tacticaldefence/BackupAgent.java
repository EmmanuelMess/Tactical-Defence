package com.emmanuelmess.tacticaldefence;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupManager;
import android.app.backup.FileBackupHelper;
import android.content.Context;

public class BackupAgent extends BackupAgentHelper { // TODO: 2015-10-17 conflict resolution

	public static final String NAME = "gameSave.save";

	// A key to uniquely identify the set of backup data
	static final String FILES_BACKUP_KEY = "files";

	public static void requestBackup(Context context) {
		BackupManager bm = new BackupManager(context);
		bm.dataChanged();
	}

	// Allocate a helper and add it to the backup agent
	@Override
	public void onCreate() {
		FileBackupHelper helper = new FileBackupHelper(this, NAME);
		addHelper(FILES_BACKUP_KEY, helper);
	}

}