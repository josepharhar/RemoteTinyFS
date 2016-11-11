// This file controls the UI of the page

polyFS_visualizer.load_disk = {
   load: function (disk, token) {
      console.log("loading: " + disk);
      $.ajax({
         type: "GET",
         dataType: 'binary',
         processData: false,
         responseType:'arraybuffer',
         //url: "disks/" + disk,
         url: "disk",
         data: "disk=" + disk + "&token=" + token
      })
      .done(function(result) {
         polyFS_visualizer.disk_config.load(disk);

         // metadata must be loaded before processing of data can occur
         polyFS_visualizer.disk_meta.load(disk, function() {
            polyFS_visualizer.data.process_data(new Uint8Array(result));
            polyFS_visualizer.load_disk.reload_page(disk);
         });
         $("#diskModal-close").click();
      })
      .fail(function() {
         warning( "disk does not exist" );
         polyFS_visualizer.load_disk.clear_page();
         $("#diskModal-close").click();
      })
   },

   reload_page: function (name) {
      polyFS_visualizer.draw.update_canvas(polyFS_visualizer.data.blocks);
      polyFS_visualizer.draw.update_legend();
      polyFS_visualizer.history.update_history(name);
      polyFS_visualizer.block_meta.update_block_meta(null);
      polyFS_visualizer.disk_meta.update(null);
   },

   clear_page: function(){
      polyFS_visualizer.data.clear_disk();
      polyFS_visualizer.config.reset();
      polyFS_visualizer.load_disk.reload_page();
   }
}
