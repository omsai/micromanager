project(MMCORE)
add_library(MMCore SHARED 
  MMCore.cpp
  CircularBuffer.cpp
  CoreCallback.cpp
  CoreProperty.cpp
  PluginManager.cpp
  FastLogger.cpp
  Host.cpp
  )
