# REQ-0004 TSID

```
Replace the used UUID based file name prefix by a [TSID](https://github.com/f4b6a3/tsid-creator) based prefix, which is shorter
```

## Result:

I've successfully replaced UUID-based file naming with TSID-based naming throughout the Android archiver app. Here's what was implemented:

Changes Made:

1. Added TSID dependency:
   - Added tsid-creator 5.2.6 to libs.versions.toml                          
   - Added JitPack repository dependency in app build configuration
2. Updated FileUtils:
   - Replaced UUID.randomUUID() with TsidCreator.getTsid()                  
   - Added comprehensive documentation explaining TSID benefits
   - Maintained same file naming pattern: {TSID}-{originalFileName}
3. Updated FileRepository:
   - Updated variable names from uuid to tsid                                                                                                                                                                                
   - Updated log messages to reflect TSID format
   - Maintained backward compatibility with existing parsing logic
4. Enhanced tests:
   - Updated generateUniqueFileName test for TSID format
   - Added TSID length validation (13 characters vs 36 for UUID)
   - Added uniqueness verification test
5. Updated documentation:
   - Updated CLAUDE.md with TSID information
   - Documented TSID benefits and dependency

TSID Benefits:

- 63% shorter: 13 characters vs 36 characters (UUID)
- Time-ordered: Lexicographically sortable by creation time
- URL-friendly: Uses only safe characters (no hyphens except separator)
- Collision-resistant: Practically unique across time and nodes
- Better performance: Faster generation and smaller memory footprint

The implementation maintains full backward compatibility while providing more efficient file naming. Existing UUID-named files will continue to work, while new files will use the shorter TSID format.
