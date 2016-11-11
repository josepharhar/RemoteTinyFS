// This file controls the canvases

$.jCanvas.defaults.fromCenter = false;
      
polyFS_visualizer.draw = {
   
   // draws the main canvas on the main page
   update_canvas: function(blocks){
      console.log("updating file system canvas");

      // padding around the block
      var padding = 10;
      // width of the block
      var width = 40;
      // number of blocks
      var numblocks = blocks.length;
      // length of a row of blocks
      var row = 8;
      // corner
      var radius = 5;
      
      // set height of canvas
      $("#fsCanvasDiv").html('<canvas id="fsCanvas"></canvas>');
      var canvas = document.getElementById('fsCanvas');
      canvas.height = Math.ceil(blocks.length / 8) * (padding * 2 + width);
      canvas.width = $('#fsCanvasDiv').width();
      
      $('canvas').detectPixelRatio(function(ratio) {});

      $('#fsCanvas').removeLayers();
      $('#fsCanvas').clearCanvas();
      
      var block = padding*2 + width;
      for(i = 0; i < numblocks; i++){

         // calculate the x and y coordinates
         var x_val = block*(i % row) + padding;
         var y_val = block*Math.floor(i/row) + padding;

         // calculate the color
         var color_id = blocks[i].get_byte(polyFS_visualizer.config.block_config.bytes.indexOf("type"));
         var color = polyFS_visualizer.config.get_display_config("block", "fillstyle", polyFS_visualizer.config.get_data_config("type", color_id));

         // draw the rectangle
         $('#fsCanvas').drawRect({
            layer: true,
            name: "" + i,
            fillStyle: color,
            x: x_val + .5, 
            y: y_val + .5,
            width: width,
            height: width,
            cornerRadius: radius,
            strokeStyle: '#000000',
            strokeWidth: 3,
            click: function(layer) {
               $("#block-number").val(Number(layer.name));
               $('#blockModal').modal('show');
            },
            mouseover: function(layer) {
               polyFS_visualizer.block_meta.update_block_meta(layer.name);
            },
            mouseout: function(layer) {
               polyFS_visualizer.block_meta.update_block_meta(null);
            },
         });
      }
      console.log("done update");
   },

   // draws the legend panel canvas on the main page
   update_legend: function(){
      console.log("drawing legend");
      
      
      legend_items = polyFS_visualizer.config.display_config.block.fillstyle.mapping;
      legend_items["unrecognised"] = polyFS_visualizer.config.display_config.block.fillstyle.default;
         
      $("#legendCanvasDiv").html('<canvas id="legendCanvas" width="200" height="' + Object.keys(legend_items).length * 35 + '"></canvas>');
      
      var canvas = document.getElementById('legendCanvas');
      $('canvas').detectPixelRatio(function(ratio) {});
     
      polyFS_visualizer.draw_utils.draw_legend("legendCanvas", 0, 0, legend_items, "block");
   }
}
