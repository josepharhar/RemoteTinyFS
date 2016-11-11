// this file controls the disk metadata panel on the main page

polyFS_visualizer.disk_meta = {

   metadata: null,

   load: function (disk, callback) {
      console.log("loading meta data: " + disk);
      $.ajax({
         type: "GET",
         dataType: 'binary',
         processData: false,
         responseType:'text',
         url: "disks/" + disk + ".meta",
      })
      .done(function(result) {
         console.log("found disk metadata");
         polyFS_visualizer.disk_meta.metadata = polyFS_visualizer.disk_meta.process(result);
         polyFS_visualizer.disk_meta.update(callback);
      })
      .fail(function() {
         console.log("could not find disk metadata");
         warning( "disk metadata does not exist" );
         polyFS_visualizer.disk_meta.metadata = polyFS_visualizer.config.disk_config;
         polyFS_visualizer.disk_meta.update(callback);
      })
   },

   process: function(meta_string){

      // parse the json
      try{
         meta = JSON.parse(meta_string);
      }
      catch(e){
         warning("metadata failed to load: " + e.message);
         return;
      }
      return meta;
   },

   update: function(callback){
      
      // build the html text
      meta_text = "";

      // add warning if disk_meta is not set
      if(!polyFS_visualizer.disk_meta.metadata){
         meta_text += "<font color=\"red\"><center>metadata failed to load,</center><center>using default values instead</center></font>"
      }
      else {
      
         // metadata must have block_size
         if(!("block_size" in polyFS_visualizer.disk_meta.metadata)){
            warning("disk metadata missing block size, using default");
            polyFS_visualizer.disk_meta.metadata["block_size"] = polyFS_visualizer.default_config.disk_config.block_size;
         }

         for(var key in polyFS_visualizer.disk_meta.metadata){
            meta_text += "<b>" + key + "</b>: ";
            meta_text += polyFS_visualizer.disk_meta.metadata[key] + "<br />"; 
         }
         $("#diskmeta").html(meta_text);

         console.log("updating config with disk metadata");
         polyFS_visualizer.config.disk_config = polyFS_visualizer.disk_meta.metadata;
      }

      // callback so disk processing can start
      if (callback) {
         callback();
      }
   }
}
