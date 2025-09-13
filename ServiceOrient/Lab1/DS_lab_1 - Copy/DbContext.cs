using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Reflection.Emit;

namespace DS_lab_1
{


    public class HockeyContext : DbContext
    {
        public HockeyContext(DbContextOptions<HockeyContext> options)
            : base(options) { }

        public DbSet<DS_lab_1.Models.Player> Players { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<DS_lab_1.Models.Player>()
                .HasKey(p => p.PlayerId);

            modelBuilder.Entity<DS_lab_1.Models.Player>()
                .Property(p => p.FName).HasMaxLength(50);

            modelBuilder.Entity<DS_lab_1.Models.Player>()
                .Property(p => p.SName).HasMaxLength(50);

            modelBuilder.Entity<DS_lab_1.Models.Player>()
                .Property(p => p.Position).HasMaxLength(5);
        }
    }
}
