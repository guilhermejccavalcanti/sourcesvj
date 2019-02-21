<<<<<<< MINE
||||||| BASE
				if (context.getBoolean("stop"))
					break;
=======
				if (log.isDebugEnabled())
					log.debugf("next loop '%s'", context.getBoolean("stop"));
				if (context.getBoolean("stop"))
					break;
>>>>>>> YOURS

