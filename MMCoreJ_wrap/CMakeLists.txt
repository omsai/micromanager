
find_package( SWIG REQUIRED )
include( ${SWIG_USE_FILE} )

find_package( Java REQUIRED )
include( UseJava )

# Discover JNI path
if( NOT JDK_PATH )
  get_filename_component( JAVAC_REALPATH ${Java_JAVAC_EXECUTABLE} REALPATH )
  get_filename_component( JDK_HINT ${JAVAC_REALPATH} PATH )
  get_filename_component( JDK_HINT ${JDK_HINT} PATH )

  set( JDK_PATH ${JDK_HINT} CACHE PATH "Location of the JDK" )
 endif()

find_path( JDK_INCLUDE jni.h HINTS ${JDK_PATH}/include )
string( TOLOWER ${CMAKE_SYSTEM_NAME} JDK_PLATFORM_GUESS )
set( JDK_PLATFORM_TYPE ${JDK_PLATFORM_GUESS} CACHE STRING "Name of the platform-specific include directory for jni_md.h" )
find_path( JDK_PLATFORM_INCLUDE jni_md.h HINTS ${JDK_INCLUDE}/${JDK_PLATFORM_TYPE} ${JDK_INCLUDE} )

include_directories( ${JDK_INCLUDE} ${JDK_PLATFORM_INCLUDE} )

set_source_files_properties( MMCoreJ.i PROPERTIES CPLUSPLUS ON )
set( SWIG_MODULE_MMCoreJ_EXTRA_DEPS MMCore )
swig_add_module( MMCoreJ_wrap java MMCoreJ.i )
swig_link_libraries( MMCoreJ_wrap MMCore MMDevice )
add_jar( MMCoreJ TaggedImage.java *.java ../mmstudio/src/org/json/*.java )
