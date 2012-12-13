project(MMCORE)

set( MMCore_SOURCES 
  MMCore.cpp MMCore.h
  Configuration.cpp Configuration.h
  CircularBuffer.cpp CircularBuffer.h
  CoreCallback.cpp CoreCallback.h
  CoreProperty.cpp CoreProperty.h
  PluginManager.cpp PluginManager.h
  FastLogger.cpp FastLogger.h
  Host.cpp Host.h
  )

add_library(MMCore SHARED ${MMCore_SOURCES} )
target_link_libraries( MMCore MMDevice dl z ${Boost_LIBRARIES} )

if( APPLE )
find_library( FRAMEWORK_CoreFoundation CoreFoundation )
find_library( FRAMEWORK_IOKit IOKit  )

target_link_libraries( MMCore ${FRAMEWORK_CoreFoundation} ${FRAMEWORK_IOKit} )
endif()

# This is mainly built to bundle with the JNI and Python bindings
add_library(MMCore_static STATIC ${MMCore_SOURCES} )
set_target_properties( MMCore_static PROPERTIES POSITION_INDEPENDENT_CODE TRUE )
