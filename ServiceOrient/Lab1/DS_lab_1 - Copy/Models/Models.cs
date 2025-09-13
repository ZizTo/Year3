using System.ComponentModel.DataAnnotations;

namespace DS_lab_1.Models
{
    public class Player
    {
        [Key]
        public string PlayerId { get; set; }

        public int Jersey { get; set; }

        [Required]
        public string FName { get; set; }

        [Required]
        public string SName { get; set; }

        [Required]
        public string Position { get; set; }

        public DateTime Birthday { get; set; }

        public int Weight { get; set; }

        public int Height { get; set; }

        public string BirthCity { get; set; }

        public string BirthState { get; set; }
    }


    public class PlayerUpdateDTO
    {
        [Key]
        public string PlayerId { get; set; }

        public string Birthday { get; set; }

        public string BirthState { get; set; }
    }
}
